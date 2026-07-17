package com.g2.demo.pruebas.juan.unitarias;

import com.g2.demo.dto.HistorialPrecioItem;
import com.g2.demo.dto.HistorialPreciosResponse;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuanHU09UnitariasTest {

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

    @InjectMocks
    private IngresoInventarioService ingresoInventarioService;

    @Test
    void consultarHistorialPrecios_filtradoPorProductoYProveedor_muestraDatosOrdenadosYPromedio() {
        DetalleIngreso compraReciente = detalleIngreso(
                LocalDateTime.of(2026, 7, 10, 9, 0),
                "Shampoo",
                "Proveedor Lima",
                "4.00",
                "12.00");
        DetalleIngreso compraAntigua = detalleIngreso(
                LocalDateTime.of(2026, 7, 1, 15, 30),
                "Shampoo",
                "Proveedor Lima",
                "2.00",
                "10.00");
        when(detalleRepository.findByProducto_IdAndProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(1L, 2L))
                .thenReturn(List.of(compraReciente, compraAntigua));

        HistorialPreciosResponse response = ingresoInventarioService.consultarHistorialPrecios(1L, 2L);

        verify(detalleRepository).findByProducto_IdAndProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(1L, 2L);
        assertBigDecimalEquals("11.00", response.getPrecioPromedio());
        assertEquals(2, response.getHistorial().size());

        HistorialPrecioItem primero = response.getHistorial().getFirst();
        assertEquals(LocalDateTime.of(2026, 7, 10, 9, 0), primero.getFecha());
        assertEquals("Shampoo", primero.getProducto());
        assertEquals("Proveedor Lima", primero.getProveedor());
        assertBigDecimalEquals("4.00", primero.getCantidad());
        assertBigDecimalEquals("12.00", primero.getCostoUnitario());
        assertBigDecimalEquals("48.00", primero.getCostoTotal());

        HistorialPrecioItem segundo = response.getHistorial().get(1);
        assertEquals(LocalDateTime.of(2026, 7, 1, 15, 30), segundo.getFecha());
        assertBigDecimalEquals("10.00", segundo.getCostoUnitario());
        assertBigDecimalEquals("20.00", segundo.getCostoTotal());
    }

    @Test
    void consultarHistorialPrecios_sinFiltros_devuelveTodosLosRegistrosYPrecioPromedio() {
        DetalleIngreso compraReciente = detalleIngreso(
                LocalDateTime.of(2026, 7, 10, 9, 0),
                "Shampoo",
                "Proveedor Lima",
                "4.00",
                "12.00");
        DetalleIngreso compraAntigua = detalleIngreso(
                LocalDateTime.of(2026, 7, 1, 15, 30),
                "Jabón",
                "Proveedor Callao",
                "3.00",
                "10.00");
        when(detalleRepository.findAllByOrderByIngresoInventarioFechaIngresoDesc())
                .thenReturn(List.of(compraReciente, compraAntigua));

        HistorialPreciosResponse response = ingresoInventarioService.consultarHistorialPrecios(null, null);

        verify(detalleRepository).findAllByOrderByIngresoInventarioFechaIngresoDesc();
        assertBigDecimalEquals("11.00", response.getPrecioPromedio());
        assertEquals(2, response.getHistorial().size());
        assertEquals("Shampoo", response.getHistorial().get(0).getProducto());
        assertEquals("Proveedor Callao", response.getHistorial().get(1).getProveedor());
    }

    @Test
    void consultarHistorialPrecios_filtradoSoloPorProducto_muestraSoloProductoYPrecioPromedio() {
        DetalleIngreso compraReciente = detalleIngreso(
                LocalDateTime.of(2026, 7, 10, 9, 0),
                "Shampoo",
                "Proveedor Lima",
                "4.00",
                "12.00");
        DetalleIngreso compraAntigua = detalleIngreso(
                LocalDateTime.of(2026, 7, 1, 15, 30),
                "Shampoo",
                "Proveedor Callao",
                "2.00",
                "10.00");
        when(detalleRepository.findByProducto_IdOrderByIngresoInventarioFechaIngresoDesc(1L))
                .thenReturn(List.of(compraReciente, compraAntigua));

        HistorialPreciosResponse response = ingresoInventarioService.consultarHistorialPrecios(1L, null);

        verify(detalleRepository).findByProducto_IdOrderByIngresoInventarioFechaIngresoDesc(1L);
        assertBigDecimalEquals("11.00", response.getPrecioPromedio());
        assertEquals(2, response.getHistorial().size());
        assertEquals("Shampoo", response.getHistorial().get(0).getProducto());
        assertEquals("Proveedor Callao", response.getHistorial().get(1).getProveedor());
    }

    @Test
    void consultarHistorialPrecios_filtradoSoloPorProveedor_muestraSoloProveedorYPrecioPromedio() {
        DetalleIngreso compraReciente = detalleIngreso(
                LocalDateTime.of(2026, 7, 10, 9, 0),
                "Shampoo",
                "Proveedor Lima",
                "4.00",
                "12.00");
        DetalleIngreso compraAntigua = detalleIngreso(
                LocalDateTime.of(2026, 7, 1, 15, 30),
                "Jabón",
                "Proveedor Lima",
                "3.00",
                "10.00");
        when(detalleRepository.findByProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(2L))
                .thenReturn(List.of(compraReciente, compraAntigua));

        HistorialPreciosResponse response = ingresoInventarioService.consultarHistorialPrecios(null, 2L);

        verify(detalleRepository).findByProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(2L);
        assertBigDecimalEquals("11.00", response.getPrecioPromedio());
        assertEquals(2, response.getHistorial().size());
        assertEquals("Proveedor Lima", response.getHistorial().get(0).getProveedor());
        assertEquals("Jabón", response.getHistorial().get(1).getProducto());
    }

    private DetalleIngreso detalleIngreso(LocalDateTime fecha, String productoNombre, String proveedorNombre,
                                          String cantidad, String costoUnitario) {
        Producto producto = new Producto();
        producto.setNombre(productoNombre);

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(proveedorNombre);

        IngresoInventario ingreso = new IngresoInventario();
        ingreso.setFechaIngreso(fecha);

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIngresoInventario(ingreso);
        detalle.setProducto(producto);
        detalle.setProveedor(proveedor);
        detalle.setCantidad(new BigDecimal(cantidad));
        detalle.setCostoUnitario(new BigDecimal(costoUnitario));
        detalle.setCostoTotal(new BigDecimal(cantidad).multiply(new BigDecimal(costoUnitario)));
        return detalle;
    }

    private void assertBigDecimalEquals(String esperado, BigDecimal actual) {
        assertTrue(new BigDecimal(esperado).compareTo(actual) == 0);
    }
}
