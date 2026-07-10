package com.g2.demo.service;

import com.g2.demo.dto.ReporteConsumoCategoria;
import com.g2.demo.dto.ReporteConsumoItem;
import com.g2.demo.dto.ReporteConsumoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteConsumoPdfService {

    private final PdfDocumentBuilder pdfDocumentBuilder;

    public ReporteConsumoPdfService() {
        this(new PdfDocumentBuilder());
    }

    public ReporteConsumoPdfService(PdfDocumentBuilder pdfDocumentBuilder) {
        this.pdfDocumentBuilder = pdfDocumentBuilder;
    }

    public byte[] generar(ReporteConsumoResponse reporte) {
        return pdfDocumentBuilder.generar(construirLineas(reporte));
    }

    private List<String> construirLineas(ReporteConsumoResponse reporte) {
        List<String> lineas = new ArrayList<>();
        lineas.add("Hotel Piramide - Reporte de consumo de inventario");
        lineas.add("Periodo: " + reporte.getFechaInicio() + " al " + reporte.getFechaFin());
        lineas.add("Fecha de generacion: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        lineas.add("");
        if (reporte.getCategorias().isEmpty()) {
            lineas.add("No hay movimientos de consumo en el rango seleccionado.");
            return lineas;
        }
        for (ReporteConsumoCategoria categoria : reporte.getCategorias()) {
            lineas.add("Categoria: " + categoria.getCategoria());
            for (ReporteConsumoItem producto : categoria.getProductos()) {
                lineas.add("  " + producto.getProducto() + " - Cantidad consumida: " + producto.getCantidadConsumida());
            }
            lineas.add("Subtotal " + categoria.getCategoria() + ": " + categoria.getSubtotal());
            lineas.add("");
        }
        lineas.add("Total general consumido: " + reporte.getTotalGeneral());
        return lineas;
    }
}
