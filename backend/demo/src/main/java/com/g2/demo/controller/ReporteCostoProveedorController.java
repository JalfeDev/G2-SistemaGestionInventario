package com.g2.demo.controller;

import com.g2.demo.dto.ReporteCostoProveedorResponse;
import com.g2.demo.service.ReporteCostoProveedorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes/costos-proveedor")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
public class ReporteCostoProveedorController {

    private final ReporteCostoProveedorService service;

    public ReporteCostoProveedorController(ReporteCostoProveedorService service) {
        this.service = service;
    }

    @GetMapping
    public ReporteCostoProveedorResponse consultar(
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return service.generar(proveedorId, fechaInicio, fechaFin);
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-costos-proveedor.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(service.generarPdf(proveedorId, fechaInicio, fechaFin));
    }
}
