package com.g2.demo.controller;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.service.IngresoInventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ingresos-inventario")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR','ALMACEN')")
public class IngresoInventarioController {

    private final IngresoInventarioService service;

    public IngresoInventarioController(IngresoInventarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<DetalleIngreso> listar() {
        return service.listarHistorial();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleIngreso registrar(@RequestBody RegistrarEntradaRequest request, Principal principal) {
        return service.registrar(request, principal.getName());
    }
}
