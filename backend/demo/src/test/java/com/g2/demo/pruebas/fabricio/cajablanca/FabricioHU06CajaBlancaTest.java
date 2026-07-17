package com.g2.demo.pruebas.fabricio.cajablanca;

import com.g2.demo.dto.RegistrarDistribucionRequest;
import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.entity.DistribucionInsumos;
import com.g2.demo.entity.Habitacion;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleDistribucionRepository;
import com.g2.demo.repository.DistribucionInsumosRepository;
import com.g2.demo.repository.HabitacionRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.DistribucionInsumosService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class FabricioHU06CajaBlancaTest {

    @Mock
    private DistribucionInsumosRepository distribucionRepository;
    @Mock
    private DetalleDistribucionRepository detalleRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;
    @Mock
    private NotificacionStockService notificacionStockService;

    private DistribucionInsumosService service;

    @BeforeEach 
    void setUp() {
        service = new DistribucionInsumosService(distribucionRepository, detalleRepository, productoRepository,
                habitacionRepository, usuarioRepository, movimientoRepository, notificacionStockService);
    }

    @Test
    void caminoExitosoDescuentaStockGuardaDetalleMovimientoYNotificaStockCritico() {
        Producto producto = producto(1L, "Jabon", "9.00");
        Habitacion habitacion = habitacion(501L);
        Usuario usuario = new Usuario();
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(habitacionRepository.findById(501L)).thenReturn(Optional.of(habitacion));
        when(usuarioRepository.findByUsernameOrEmail("house", "house")).thenReturn(Optional.of(usuario));

        DetalleDistribucion detalle = service.registrar(distribucion(1L, 501L, "4.00"), "house");

        assertSame(producto, detalle.getProducto());
        assertSame(habitacion, detalle.getDistribucion().getHabitacion());
        assertSame(usuario, detalle.getDistribucion().getUsuario());
        assertEquals(0, new BigDecimal("5.00").compareTo(producto.getStockActual()));
        verify(distribucionRepository).save(any(DistribucionInsumos.class));
        verify(detalleRepository).save(detalle);
        verify(productoRepository).save(producto);
        verify(notificacionStockService).evaluarStockCritico(producto);

        ArgumentCaptor<MovimientoInventario> movimiento = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).save(movimiento.capture());
        assertEquals("SALIDA", movimiento.getValue().getTipoMovimiento());
        assertEquals(0, new BigDecimal("9.00").compareTo(movimiento.getValue().getStockAnterior()));
        assertEquals(0, new BigDecimal("5.00").compareTo(movimiento.getValue().getStockNuevo()));
    }

    @Test
    void stockInsuficienteCortaAntesDeBuscarHabitacionUsuarioOPersistir() {
        Producto producto = producto(1L, "Jabon", "2.00");
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(distribucion(1L, 501L, "3.00"), "house"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(habitacionRepository, never()).findById(any());
        verify(usuarioRepository, never()).findByUsernameOrEmail(any(), any());
        verify(distribucionRepository, never()).save(any());
        verify(detalleRepository, never()).save(any());
        verify(productoRepository, never()).save(producto);
        verify(movimientoRepository, never()).save(any());
        verify(notificacionStockService, never()).evaluarStockCritico(any());
    }

    @Test
    void cantidadInvalidaCortaAntesDeConsultarProductoOPersistir() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registrar(distribucion(1L, 501L, "0"), "house"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("La cantidad debe ser mayor a cero", exception.getReason());
        verify(productoRepository, never()).findWithLockById(any());
        verify(distribucionRepository, never()).save(any());
        verify(detalleRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void listarHistorialFiltraPorFechaYHabitacion() {
        DetalleDistribucion dentro = detalleConFechaYHabitacion(LocalDateTime.of(2026, 7, 10, 9, 0), 501L);
        DetalleDistribucion otraHabitacion = detalleConFechaYHabitacion(LocalDateTime.of(2026, 7, 10, 9, 0), 502L);
        DetalleDistribucion fueraDeRango = detalleConFechaYHabitacion(LocalDateTime.of(2026, 6, 30, 9, 0), 501L);
        when(detalleRepository.findAllByOrderByDistribucionFechaDesc())
                .thenReturn(List.of(dentro, otraHabitacion, fueraDeRango));

        List<DetalleDistribucion> resultado = service.listarHistorial(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31), 501L);

        assertEquals(List.of(dentro), resultado);
    }

    private RegistrarDistribucionRequest distribucion(Long productoId, Long habitacionId, String cantidad) {
        RegistrarDistribucionRequest request = new RegistrarDistribucionRequest();
        request.setProductoId(productoId);
        request.setHabitacionId(habitacionId);
        request.setCantidad(new BigDecimal(cantidad));
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

    private Habitacion habitacion(Long id) {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(id);
        habitacion.setNumero("501");
        return habitacion;
    }

    private DetalleDistribucion detalleConFechaYHabitacion(LocalDateTime fecha, Long habitacionId) {
        Habitacion habitacion = habitacion(habitacionId);
        DistribucionInsumos distribucion = new DistribucionInsumos();
        distribucion.setFecha(fecha);
        distribucion.setHabitacion(habitacion);
        DetalleDistribucion detalle = new DetalleDistribucion();
        detalle.setDistribucion(distribucion);
        return detalle;
    }
}
