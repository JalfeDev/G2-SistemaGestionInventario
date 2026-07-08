package com.g2.demo.controller;

import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.repository.DetalleDistribucionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-distribucion")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR','HOUSEKEEPING')")
public class DetalleDistribucionController {

    private final DetalleDistribucionRepository repository;

    public DetalleDistribucionController(DetalleDistribucionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DetalleDistribucion> listar() {
        return repository.findAllByOrderByDistribucionFechaDesc();
    }

    @GetMapping("/{id}")
    public DetalleDistribucion buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de distribucion no encontrado"));
    }
}
