package com.g2.demo.pruebas.fabricio.unitarias;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleIngresoRepository;
import com.g2.demo.repository.IngresoInventarioRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.ProveedorRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FabricioHU04UnitariasTest {

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

    private IngresoInventarioService service;

    @BeforeEach
    void setUp() {
        service = new IngresoInventarioService(ingresoRepository, detalleRepository, productoRepository,
                proveedorRepository, usuarioRepository, movimientoRepository, notificacionStockService);
    }

    @Test
    void registrarEntradaValidaAumentaStockYGuardaMovimiento() {
        Producto producto = producto(1L, "Shampoo", "10.00");
        Proveedor proveedor = new Proveedor();
        Usuario usuario = new Usuario();
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(proveedorRepository.findById(2L)).thenReturn(Optional.of(proveedor));
        when(usuarioRepository.findByUsernameOrEmail("fabricio", "fabricio")).thenReturn(Optional.of(usuario));

        DetalleIngreso detalle = service.registrar(entrada(1L, 2L, "5.00", "3.50"), "fabricio");

        assertSame(producto, detalle.getProducto());
        assertSame(proveedor, detalle.getProveedor());
        assertSame(usuario, detalle.getIngresoInventario().getUsuario());
        assertEquals(0, new BigDecimal("15.00").compareTo(producto.getStockActual()));
        assertEquals(0, new BigDecimal("17.50").compareTo(detalle.getCostoTotal()));
        verify(ingresoRepository).save(any(IngresoInventario.class));
        verify(detalleRepository).save(detalle);
        verify(productoRepository).save(producto);
        verify(notificacionStockService).evaluarStockCritico(producto);

        ArgumentCaptor<MovimientoInventario> movimiento = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).save(movimiento.capture());
        assertEquals("ENTRADA", movimiento.getValue().getTipoMovimiento());
        assertEquals(0, new BigDecimal("10.00").compareTo(movimiento.getValue().getStockAnterior()));
        assertEquals(0, new BigDecimal("15.00").compareTo(movimiento.getValue().getStockNuevo()));
    }

    @Test
    void registrarEntradaConCantidadInvalidaLanzaExcepcion() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entrada(1L, 2L, "0", "3.50"), "fabricio"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("La cantidad debe ser mayor a cero", exception.getReason());
        verify(productoRepository, never()).findWithLockById(any());
        verify(ingresoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrarEntradaConProductoInexistenteLanzaExcepcion() {
        when(productoRepository.findWithLockById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entrada(99L, 2L, "5.00", "3.50"), "fabricio"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Producto no encontrado", exception.getReason());
        verify(proveedorRepository, never()).findById(any());
        verify(ingresoRepository, never()).save(any());
        verify(detalleRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrarEntradaConProveedorInexistenteLanzaExcepcion() {
        Producto producto = producto(1L, "Shampoo", "10.00");
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(proveedorRepository.findById(77L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(entrada(1L, 77L, "5.00", "3.50"), "fabricio"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Proveedor no encontrado", exception.getReason());
        assertEquals(0, new BigDecimal("10.00").compareTo(producto.getStockActual()));
        verify(usuarioRepository, never()).findByUsernameOrEmail(any(), any());
        verify(ingresoRepository, never()).save(any());
        verify(detalleRepository, never()).save(any());
        verify(productoRepository, never()).save(producto);
        verify(movimientoRepository, never()).save(any());
    }

    private RegistrarEntradaRequest entrada(Long productoId, Long proveedorId, String cantidad, String costoUnitario) {
        RegistrarEntradaRequest request = new RegistrarEntradaRequest();
        request.setProductoId(productoId);
        request.setProveedorId(proveedorId);
        request.setCantidad(new BigDecimal(cantidad));
        request.setCostoUnitario(new BigDecimal(costoUnitario));
        request.setObservacion("Compra de insumos");
        return request;
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
