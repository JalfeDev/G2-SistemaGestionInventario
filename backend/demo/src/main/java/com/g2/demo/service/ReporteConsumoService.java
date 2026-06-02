package com.g2.demo.service;

import com.g2.demo.dto.ReporteConsumoCategoria;
import com.g2.demo.dto.ReporteConsumoItem;
import com.g2.demo.dto.ReporteConsumoResponse;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.repository.MovimientoInventarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReporteConsumoService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ReporteConsumoPdfService pdfService;

    public ReporteConsumoService(MovimientoInventarioRepository movimientoRepository,
                                 ReporteConsumoPdfService pdfService) {
        this.movimientoRepository = movimientoRepository;
        this.pdfService = pdfService;
    }

    public ReporteConsumoResponse generar(LocalDate fechaInicio, LocalDate fechaFin) {
        validarRango(fechaInicio, fechaFin);
        List<MovimientoInventario> movimientos = movimientoRepository
                .findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                        "SALIDA", fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());

        Map<String, Map<Long, Acumulado>> agrupados = new TreeMap<>();
        for (MovimientoInventario movimiento : movimientos) {
            String categoria = movimiento.getProducto().getCategoria() != null
                    ? movimiento.getProducto().getCategoria().getNombre()
                    : "Sin categoria";
            agrupados.computeIfAbsent(categoria, ignored -> new LinkedHashMap<>())
                    .compute(movimiento.getProducto().getId(), (id, actual) -> {
                        if (actual == null) {
                            return new Acumulado(movimiento.getProducto().getNombre(), movimiento.getCantidad());
                        }
                        actual.cantidad = actual.cantidad.add(movimiento.getCantidad());
                        return actual;
                    });
        }

        List<ReporteConsumoCategoria> categorias = agrupados.entrySet().stream()
                .map(entry -> {
                    List<ReporteConsumoItem> productos = entry.getValue().entrySet().stream()
                            .map(producto -> new ReporteConsumoItem(
                                    producto.getKey(),
                                    producto.getValue().nombre,
                                    entry.getKey(),
                                    producto.getValue().cantidad))
                            .toList();
                    BigDecimal subtotal = productos.stream()
                            .map(ReporteConsumoItem::getCantidadConsumida)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ReporteConsumoCategoria(entry.getKey(), subtotal, productos);
                })
                .toList();
        BigDecimal total = categorias.stream()
                .map(ReporteConsumoCategoria::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReporteConsumoResponse(fechaInicio, fechaFin, categorias, total);
    }

    public byte[] generarPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        return pdfService.generar(generar(fechaInicio, fechaFin));
    }

    private void validarRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha de inicio y fecha de fin son obligatorias");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private static class Acumulado {
        private final String nombre;
        private BigDecimal cantidad;

        private Acumulado(String nombre, BigDecimal cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }
    }
}
