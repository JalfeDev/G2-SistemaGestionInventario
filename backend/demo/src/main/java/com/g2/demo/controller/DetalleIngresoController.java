package com.g2.demo.controller;

import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.repository.DetalleIngresoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-ingreso")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR','ALMACEN')")
public class DetalleIngresoController {

    private final DetalleIngresoRepository repository;

    public DetalleIngresoController(DetalleIngresoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DetalleIngreso> listar() {
        return repository.findAllByOrderByIngresoInventarioFechaIngresoDesc();
    }

    @GetMapping("/{id}")
    public DetalleIngreso buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de ingreso no encontrado"));
    }
}
