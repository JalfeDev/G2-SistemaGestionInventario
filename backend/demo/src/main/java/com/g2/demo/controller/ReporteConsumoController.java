package com.g2.demo.controller;

import com.g2.demo.dto.ReporteConsumoResponse;
import com.g2.demo.service.ReporteConsumoService;
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
@RequestMapping("/api/reportes/consumo")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
public class ReporteConsumoController {

    private final ReporteConsumoService service;

    public ReporteConsumoController(ReporteConsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ReporteConsumoResponse consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return service.generar(fechaInicio, fechaFin);
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-consumo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(service.generarPdf(fechaInicio, fechaFin));
    }
}
