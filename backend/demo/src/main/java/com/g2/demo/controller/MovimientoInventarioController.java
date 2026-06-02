package com.g2.demo.controller;

import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.repository.MovimientoInventarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioRepository repository;

    public MovimientoInventarioController(MovimientoInventarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MovimientoInventario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MovimientoInventario buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));
    }
}
