package com.g2.demo.controller;

import com.g2.demo.dto.RegistrarDistribucionRequest;
import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.facade.HousekeepingFacade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/distribuciones-insumos")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR','HOUSEKEEPING')")
public class DistribucionInsumosController {

    private final HousekeepingFacade facade;

    public DistribucionInsumosController(HousekeepingFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<DetalleDistribucion> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long habitacionId) {
        return facade.listarHistorial(fechaInicio, fechaFin, habitacionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleDistribucion registrar(@RequestBody RegistrarDistribucionRequest request, Principal principal) {
        return facade.registrarDistribucion(request, principal.getName());
    }
}
