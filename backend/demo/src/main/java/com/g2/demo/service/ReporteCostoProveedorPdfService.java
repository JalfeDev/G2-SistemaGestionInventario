package com.g2.demo.service;

import com.g2.demo.dto.ReporteCostoProveedor;
import com.g2.demo.dto.ReporteCostoProveedorItem;
import com.g2.demo.dto.ReporteCostoProveedorResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteCostoProveedorPdfService {

    private final PdfDocumentBuilder pdfDocumentBuilder;

    public ReporteCostoProveedorPdfService(PdfDocumentBuilder pdfDocumentBuilder) {
        this.pdfDocumentBuilder = pdfDocumentBuilder;
    }

    public byte[] generar(ReporteCostoProveedorResponse reporte) {
        return pdfDocumentBuilder.generar(construirLineas(reporte));
    }

    private List<String> construirLineas(ReporteCostoProveedorResponse reporte) {
        List<String> lineas = new ArrayList<>();
        lineas.add("Hotel Piramide - Reporte de costos por proveedor");
        lineas.add("Periodo: " + formatear(reporte.getFechaInicio()) + " al " + formatear(reporte.getFechaFin()));
        lineas.add("Fecha de generacion: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        lineas.add("");
        if (reporte.getProveedores().isEmpty()) {
            lineas.add("No hay compras registradas con los filtros seleccionados.");
            return lineas;
        }
        for (ReporteCostoProveedor proveedor : reporte.getProveedores()) {
            lineas.add("Proveedor: " + proveedor.getProveedor());
            for (ReporteCostoProveedorItem item : proveedor.getItems()) {
                lineas.add("  " + item.getProducto()
                        + " - Cantidad: " + item.getCantidad()
                        + " - Precio unitario: " + item.getPrecioUnitario()
                        + " - Total: " + item.getCostoTotal());
            }
            lineas.add("Subtotal " + proveedor.getProveedor() + ": " + proveedor.getSubtotal());
            lineas.add("");
        }
        lineas.add("Total general gastado: " + reporte.getTotalGeneral());
        return lineas;
    }

    private String formatear(LocalDate fecha) {
        return fecha != null ? fecha.toString() : "sin limite";
    }
}
