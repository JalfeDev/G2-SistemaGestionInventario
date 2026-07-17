package com.g2.demo;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.DetalleSolicitud;
import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleIngresoRepository;
import com.g2.demo.repository.DetalleSolicitudRepository;
import com.g2.demo.repository.IngresoInventarioRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.ProveedorRepository;
import com.g2.demo.repository.SolicitudCompraRepository;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.IngresoInventarioService;
import com.g2.demo.service.NotificacionStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Hu11EntradaSolicitudTests {

    @Mock
    private IngresoInventarioRepository ingresoRepository;
    @Mock
    private DetalleIngresoRepository detalleRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;
    @Mock
    private NotificacionStockService notificacionStockService;
    @Mock
    private SolicitudCompraRepository solicitudCompraRepository;
    @Mock
    private DetalleSolicitudRepository detalleSolicitudRepository;

    private IngresoInventarioService service;

    @BeforeEach
    void setUp() {
        service = new IngresoInventarioService(ingresoRepository, detalleRepository, productoRepository,
                proveedorRepository, usuarioRepository, movimientoRepository, notificacionStockService,
                solicitudCompraRepository, detalleSolicitudRepository);
    }

    @Test
    void solicitudPendienteNoGeneraEntrada() {
        SolicitudCompra solicitud = solicitud("PENDIENTE");
        when(solicitudCompraRepository.findWithLockById(10L)).thenReturn(Optional.of(solicitud));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(detalleSolicitudRepository, never()).findBySolicitudId(any());
        verify(ingresoRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void solicitudRechazadaNoGeneraEntrada() {
        SolicitudCompra solicitud = solicitud("RECHAZADO");
        when(solicitudCompraRepository.findWithLockById(10L)).thenReturn(Optional.of(solicitud));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(ingresoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void solicitudAprobadaGeneraEntradaAumentaStockMovimientoYQuedaAtendida() {
        SolicitudCompra solicitud = solicitud("APROBADO");
        Producto producto = producto(1L, "Shampoo", "10.00");
        prepararSolicitudAprobada(solicitud, detalleSolicitud(solicitud, producto, "5.00"));
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(proveedorRepository.findById(2L)).thenReturn(Optional.of(new Proveedor()));
        when(usuarioRepository.findByUsernameOrEmail("almacen", "almacen")).thenReturn(Optional.of(new Usuario()));

        DetalleIngreso detalle = service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen");

        assertEquals(0, new BigDecimal("15.00").compareTo(producto.getStockActual()));
        assertSame(solicitud, detalle.getIngresoInventario().getSolicitud());
        assertEquals("ATENDIDA", solicitud.getEstado());
        verify(ingresoRepository).save(any(IngresoInventario.class));
        verify(detalleRepository).save(detalle);
        verify(productoRepository).save(producto);
        verify(solicitudCompraRepository).save(solicitud);

        ArgumentCaptor<MovimientoInventario> movimiento = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).save(movimiento.capture());
        assertEquals("ENTRADA", movimiento.getValue().getTipoMovimiento());
        assertEquals(0, new BigDecimal("10.00").compareTo(movimiento.getValue().getStockAnterior()));
        assertEquals(0, new BigDecimal("15.00").compareTo(movimiento.getValue().getStockNuevo()));
    }

    @Test
    void productoDistintoAlSolicitadoEsRechazado() {
        SolicitudCompra solicitud = solicitud("APROBADO");
        prepararSolicitudAprobada(solicitud, detalleSolicitud(solicitud, producto(2L, "Jabon", "8.00"), "5.00"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productoRepository, never()).findWithLockById(any());
        verify(ingresoRepository, never()).save(any());
        verify(solicitudCompraRepository, never()).save(solicitud);
    }

    @Test
    void cantidadCeroEsRechazadaAntesDeConsultarSolicitud() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "0"), "almacen"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(solicitudCompraRepository, never()).findWithLockById(any());
        verify(ingresoRepository, never()).save(any());
    }

    @Test
    void cantidadDistintaALaAprobadaEsRechazada() {
        SolicitudCompra solicitud = solicitud("APROBADO");
        prepararSolicitudAprobada(solicitud, detalleSolicitud(solicitud, producto(1L, "Shampoo", "10.00"), "5.00"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "4.00"), "almacen"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productoRepository, never()).findWithLockById(any());
        verify(ingresoRepository, never()).save(any());
        verify(solicitudCompraRepository, never()).save(solicitud);
    }

    @Test
    void solicitudAtendidaNoPuedeReutilizarse() {
        SolicitudCompra solicitud = solicitud("ATENDIDA");
        when(solicitudCompraRepository.findWithLockById(10L)).thenReturn(Optional.of(solicitud));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(ingresoRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void siFallaProveedorNoCambiaStockNiMarcaSolicitudAtendida() {
        SolicitudCompra solicitud = solicitud("APROBADO");
        Producto producto = producto(1L, "Shampoo", "10.00");
        prepararSolicitudAprobada(solicitud, detalleSolicitud(solicitud, producto, "5.00"));
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(proveedorRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entradaDesdeSolicitud(10L, 1L, "5.00"), "almacen"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(0, new BigDecimal("10.00").compareTo(producto.getStockActual()));
        assertEquals("APROBADO", solicitud.getEstado());
        verify(ingresoRepository, never()).save(any());
        verify(detalleRepository, never()).save(any());
        verify(productoRepository, never()).save(producto);
        verify(movimientoRepository, never()).save(any());
        verify(solicitudCompraRepository, never()).save(solicitud);
    }

    private void prepararSolicitudAprobada(SolicitudCompra solicitud, DetalleSolicitud detalle) {
        when(solicitudCompraRepository.findWithLockById(10L)).thenReturn(Optional.of(solicitud));
        when(detalleSolicitudRepository.findBySolicitudId(10L)).thenReturn(List.of(detalle));
    }

    private RegistrarEntradaRequest entradaDesdeSolicitud(Long solicitudId, Long productoId, String cantidad) {
        RegistrarEntradaRequest request = new RegistrarEntradaRequest();
        request.setSolicitudId(solicitudId);
        request.setProductoId(productoId);
        request.setProveedorId(2L);
        request.setCantidad(new BigDecimal(cantidad));
        request.setCostoUnitario(new BigDecimal("3.50"));
        request.setObservacion("Recepcion aprobada");
        return request;
    }

    private SolicitudCompra solicitud(String estado) {
        SolicitudCompra solicitud = new SolicitudCompra();
        solicitud.setId(10L);
        solicitud.setEstado(estado);
        return solicitud;
    }

    private DetalleSolicitud detalleSolicitud(SolicitudCompra solicitud, Producto producto, String cantidad) {
        DetalleSolicitud detalle = new DetalleSolicitud();
        detalle.setSolicitud(solicitud);
        detalle.setProducto(producto);
        detalle.setCantidadSolicitada(new BigDecimal(cantidad));
        return detalle;
    }

    private Producto producto(Long id, String nombre, String stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setStockActual(new BigDecimal(stock));
        producto.setStockMinimo(BigDecimal.ZERO);
        return producto;
    }
}
