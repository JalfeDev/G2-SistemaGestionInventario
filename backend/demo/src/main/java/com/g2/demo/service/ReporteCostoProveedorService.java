package com.g2.demo.service;

import com.g2.demo.dto.ReporteCostoProveedor;
import com.g2.demo.dto.ReporteCostoProveedorItem;
import com.g2.demo.dto.ReporteCostoProveedorResponse;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.repository.DetalleIngresoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReporteCostoProveedorService {

    private static final LocalDateTime DESDE_POR_DEFECTO = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime HASTA_POR_DEFECTO = LocalDateTime.of(9999, 12, 31, 23, 59);

    private final DetalleIngresoRepository detalleRepository;
    private final ReporteCostoProveedorPdfService pdfService;

    public ReporteCostoProveedorService(DetalleIngresoRepository detalleRepository,
                                        ReporteCostoProveedorPdfService pdfService) {
        this.detalleRepository = detalleRepository;
        this.pdfService = pdfService;
    }

    //HU - Reporte de costos por proveedor
    public ReporteCostoProveedorResponse generar(Long proveedorId, LocalDate fechaInicio, LocalDate fechaFin) {
        validarRango(fechaInicio, fechaFin);
        LocalDateTime desde = fechaInicio != null ? fechaInicio.atStartOfDay() : DESDE_POR_DEFECTO;
        LocalDateTime hastaExclusiva = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay() : HASTA_POR_DEFECTO;

        List<DetalleIngreso> detalles = proveedorId != null
                ? detalleRepository.findByProveedor_IdAndIngresoInventario_FechaIngresoGreaterThanEqualAndIngresoInventario_FechaIngresoLessThanOrderByIngresoInventario_FechaIngresoDesc(
                proveedorId, desde, hastaExclusiva)
                : detalleRepository.findByIngresoInventario_FechaIngresoGreaterThanEqualAndIngresoInventario_FechaIngresoLessThanOrderByIngresoInventario_FechaIngresoDesc(
                desde, hastaExclusiva);

        Map<String, List<DetalleIngreso>> agrupadosPorProveedor = new TreeMap<>();
        for (DetalleIngreso detalle : detalles) {
            agrupadosPorProveedor.computeIfAbsent(detalle.getProveedor().getNombre(), ignorado -> new ArrayList<>())
                    .add(detalle);
        }

        List<ReporteCostoProveedor> proveedores = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;
        for (Map.Entry<String, List<DetalleIngreso>> entry : agrupadosPorProveedor.entrySet()) {
            List<ReporteCostoProveedorItem> items = entry.getValue().stream()
                    .map(detalle -> new ReporteCostoProveedorItem(
                            detalle.getIngresoInventario().getFechaIngreso(),
                            detalle.getProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getCostoUnitario(),
                            detalle.getCostoTotal()))
                    .toList();
            BigDecimal subtotal = items.stream()
                    .map(ReporteCostoProveedorItem::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            proveedores.add(new ReporteCostoProveedor(entry.getKey(), items, subtotal));
            totalGeneral = totalGeneral.add(subtotal);
        }

        return new ReporteCostoProveedorResponse(fechaInicio, fechaFin, proveedores, totalGeneral);
    }

    public byte[] generarPdf(Long proveedorId, LocalDate fechaInicio, LocalDate fechaFin) {
        return pdfService.generar(generar(proveedorId, fechaInicio, fechaFin));
    }

    private void validarRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}
