package com.g2.demo;

import com.g2.demo.dto.RegistrarDistribucionRequest;
import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.dto.ReporteConsumoResponse;
import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.Habitacion;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleDistribucionRepository;
import com.g2.demo.repository.DetalleIngresoRepository;
import com.g2.demo.repository.DistribucionInsumosRepository;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.HabitacionRepository;
import com.g2.demo.repository.IngresoInventarioRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.ProveedorRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.DistribucionInsumosService;
import com.g2.demo.service.IngresoInventarioService;
import com.g2.demo.service.ProductoService;
import com.g2.demo.service.ReporteConsumoPdfService;
import com.g2.demo.service.ReporteConsumoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Sprint1BusinessRulesTests {

    @Mock
    private IngresoInventarioRepository ingresoRepository;
    @Mock
    private DetalleIngresoRepository detalleIngresoRepository;
    @Mock
    private DistribucionInsumosRepository distribucionRepository;
    @Mock
    private DetalleDistribucionRepository detalleDistribucionRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;
    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    private IngresoInventarioService ingresoService;
    private DistribucionInsumosService distribucionService;

    @BeforeEach
    void setUp() {
        ingresoService = new IngresoInventarioService(ingresoRepository, detalleIngresoRepository, productoRepository,
                proveedorRepository, usuarioRepository, movimientoRepository);
        distribucionService = new DistribucionInsumosService(distribucionRepository, detalleDistribucionRepository,
                productoRepository, habitacionRepository, usuarioRepository, movimientoRepository);
    }

    @Test
    void registrarEntradaActualizaStockPersisteTotalYCreaMovimiento() {
        Producto producto = producto(1L, "Shampoo", "10");
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(proveedorRepository.findById(2L)).thenReturn(Optional.of(new Proveedor()));
        when(usuarioRepository.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(new Usuario()));

        RegistrarEntradaRequest request = new RegistrarEntradaRequest();
        request.setProductoId(1L);
        request.setProveedorId(2L);
        request.setCantidad(new BigDecimal("2"));
        request.setCostoUnitario(new BigDecimal("3.50"));

        DetalleIngreso detalle = ingresoService.registrar(request, "admin");

        assertEquals(new BigDecimal("12"), producto.getStockActual());
        assertEquals(new BigDecimal("7.00"), detalle.getCostoTotal());
        ArgumentCaptor<MovimientoInventario> movimiento = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).save(movimiento.capture());
        assertEquals("ENTRADA", movimiento.getValue().getTipoMovimiento());
    }

    @Test
    void registrarEntradaRechazaCantidadCeroSinPersistir() {
        RegistrarEntradaRequest request = new RegistrarEntradaRequest();
        request.setProductoId(1L);
        request.setProveedorId(2L);
        request.setCantidad(BigDecimal.ZERO);
        request.setCostoUnitario(BigDecimal.ONE);

        assertThrows(ResponseStatusException.class, () -> ingresoService.registrar(request, "admin"));
        verify(ingresoRepository, never()).save(any());
    }

    @Test
    void registrarDistribucionDescuentaStockYCreaMovimiento() {
        Producto producto = producto(1L, "Shampoo", "10");
        Habitacion habitacion = new Habitacion();
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));
        when(habitacionRepository.findById(4L)).thenReturn(Optional.of(habitacion));
        when(usuarioRepository.findByUsernameOrEmail("house", "house")).thenReturn(Optional.of(new Usuario()));

        RegistrarDistribucionRequest request = new RegistrarDistribucionRequest();
        request.setProductoId(1L);
        request.setHabitacionId(4L);
        request.setCantidad(new BigDecimal("3"));

        DetalleDistribucion detalle = distribucionService.registrar(request, "house");

        assertEquals(new BigDecimal("7"), producto.getStockActual());
        assertEquals(new BigDecimal("3"), detalle.getCantidad());
        ArgumentCaptor<MovimientoInventario> movimiento = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository).save(movimiento.capture());
        assertEquals("SALIDA", movimiento.getValue().getTipoMovimiento());
    }

    @Test
    void registrarDistribucionRechazaCantidadMayorAlStockSinPersistir() {
        Producto producto = producto(1L, "Shampoo", "2");
        when(productoRepository.findWithLockById(1L)).thenReturn(Optional.of(producto));

        RegistrarDistribucionRequest request = new RegistrarDistribucionRequest();
        request.setProductoId(1L);
        request.setHabitacionId(4L);
        request.setCantidad(new BigDecimal("3"));

        assertThrows(ResponseStatusException.class, () -> distribucionService.registrar(request, "house"));
        verify(distribucionRepository, never()).save(any());
    }

    @Test
    void reporteAgrupaSalidasPorCategoriaYProducto() {
        Producto producto = producto(1L, "Shampoo", "10");
        Categoria categoria = new Categoria();
        categoria.setNombre("Amenidades");
        producto.setCategoria(categoria);
        MovimientoInventario primero = movimiento(producto, "2");
        MovimientoInventario segundo = movimiento(producto, "3");
        when(movimientoRepository.findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                any(), any(), any())).thenReturn(List.of(primero, segundo));
        ReporteConsumoService reporteService = new ReporteConsumoService(movimientoRepository, new ReporteConsumoPdfService());

        ReporteConsumoResponse reporte = reporteService.generar(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertEquals(new BigDecimal("5"), reporte.getTotalGeneral());
        assertEquals(new BigDecimal("5"), reporte.getCategorias().getFirst().getSubtotal());
        assertTrue(new String(reporteService.generarPdf(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31))).startsWith("%PDF"));
    }

    @Test
    void actualizarProductoRechazaCambioDirectoDeStock() {
        Producto producto = producto(1L, "Shampoo", "10");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        ProductoRequest request = new ProductoRequest();
        request.setStockActual(new BigDecimal("9"));
        ProductoService productoService = new ProductoService(
                productoRepository, categoriaRepository, unidadMedidaRepository, movimientoRepository);

        assertThrows(ResponseStatusException.class, () -> productoService.actualizar(1L, request));
        verify(productoRepository, never()).save(any());
    }

    private Producto producto(Long id, String nombre, String stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setStockActual(new BigDecimal(stock));
        producto.setStockMinimo(BigDecimal.ZERO);
        return producto;
    }

    private MovimientoInventario movimiento(Producto producto, String cantidad) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(new BigDecimal(cantidad));
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento("SALIDA");
        return movimiento;
    }
}
