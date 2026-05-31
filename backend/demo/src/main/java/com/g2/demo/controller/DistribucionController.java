package com.g2.demo.controller;

import com.g2.demo.dto.DistribucionRequest;
import com.g2.demo.entity.Distribucion;
import com.g2.demo.service.HousekeepingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/housekeeping")
@CrossOrigin(origins = "*")
public class DistribucionController {

    @Autowired
    private HousekeepingFacade housekeepingFacade;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarConsumo(@RequestBody DistribucionRequest request) {
        try {
            Distribucion nuevaDistribucion = housekeepingFacade.registrarConsumoInsumo(request);
            return ResponseEntity.ok(nuevaDistribucion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/historial/habitacion/{id}")
    public ResponseEntity<List<Distribucion>> historialPorHabitacion(@PathVariable Long id) {
        return ResponseEntity.ok(housekeepingFacade.listarPorHabitacion(id));
    }

    @GetMapping("/historial/fechas")
    public ResponseEntity<List<Distribucion>> historialPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(housekeepingFacade.listarPorFechas(inicio, fin));
    }
}