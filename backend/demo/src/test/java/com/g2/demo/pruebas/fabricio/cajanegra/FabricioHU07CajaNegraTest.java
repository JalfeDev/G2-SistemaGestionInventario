package com.g2.demo.pruebas.fabricio.cajanegra;

import com.g2.demo.dto.ReporteConsumoResponse;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.service.ReporteConsumoPdfService;
import com.g2.demo.service.ReporteConsumoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FabricioHU07CajaNegraTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    private ReporteConsumoService service;

    @BeforeEach
    void setUp() {
        service = new ReporteConsumoService(movimientoRepository, new ReporteConsumoPdfService());
    }

    @Test
    void reporteConRangoValidoDevuelveDatosAgrupados() {
        Producto shampoo = producto(1L, "Shampoo", "Amenidades");
        when(movimientoRepository.findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                any(), any(), any())).thenReturn(List.of(
                        movimiento(shampoo, "2.00"),
                        movimiento(shampoo, "3.00")));

        ReporteConsumoResponse respuesta = service.generar(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertEquals(1, respuesta.getCategorias().size());
        assertEquals("Amenidades", respuesta.getCategorias().getFirst().getCategoria());
        assertEquals(0, new BigDecimal("5.00").compareTo(respuesta.getTotalGeneral()));
        assertEquals(0, new BigDecimal("5.00").compareTo(respuesta.getCategorias().getFirst().getSubtotal()));
    }

    @Test
    void reportePdfConRangoValidoDevuelveArchivoPdf() {
        Producto papel = producto(2L, "Papel higienico", "Limpieza");
        when(movimientoRepository.findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                any(), any(), any())).thenReturn(List.of(movimiento(papel, "4.00")));

        byte[] pdf = service.generarPdf(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertTrue(pdf.length > 0);
        assertTrue(new String(pdf, 0, 8, StandardCharsets.ISO_8859_1).startsWith("%PDF-1.4"));
    }

    @Test
    void reporteSinMovimientosDevuelveRespuestaVaciaControlada() {
        when(movimientoRepository.findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                any(), any(), any())).thenReturn(List.of());

        ReporteConsumoResponse respuesta = service.generar(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertTrue(respuesta.getCategorias().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(respuesta.getTotalGeneral()));
    }

    @Test
    void reporteConRangoInvalidoDevuelveBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.generar(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 7, 1)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", exception.getReason());
    }

    private Producto producto(Long id, String nombre, String categoriaNombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaNombre);
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setStockActual(BigDecimal.ZERO);
        producto.setStockMinimo(BigDecimal.ZERO);
        return producto;
    }

    private MovimientoInventario movimiento(Producto producto, String cantidad) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(new BigDecimal(cantidad));
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setFechaMovimiento(LocalDateTime.of(2026, 7, 10, 10, 0));
        return movimiento;
    }
}
