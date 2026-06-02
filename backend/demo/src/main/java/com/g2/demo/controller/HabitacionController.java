package com.g2.demo.controller;

import com.g2.demo.entity.Habitacion;
import com.g2.demo.repository.HabitacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionRepository repository;

    public HabitacionController(HabitacionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Habitacion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Habitacion buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitacion no encontrada"));
    }
}
