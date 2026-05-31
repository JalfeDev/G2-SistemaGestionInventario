package com.g2.demo.controller;

import com.g2.demo.dto.EntradaInsumoRequest;
import com.g2.demo.service.AlmacenFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/entradas")
public class EntradaInsumoController {

    private final AlmacenFacade almacenFacade;

    public EntradaInsumoController(AlmacenFacade almacenFacade) {
        this.almacenFacade = almacenFacade;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String registrarEntrada(@RequestBody EntradaInsumoRequest request, Principal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Acceso denegado: Token JWT ausente o inválido");
        }

        almacenFacade.registrarEntradaInsumo(request, principal.getName());
        return "Entrada registrada con éxito. Stock actualizado y trazabilidad guardada.";
    }
}