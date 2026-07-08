package com.g2.demo.controller;

import com.g2.demo.dto.RevisionSolicitudRequest;
import com.g2.demo.dto.SolicitudCompraRequest;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.service.SolicitudCompraService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-compra")
public class SolicitudCompraController {

    private final SolicitudCompraService solicitudCompraService;

    public SolicitudCompraController(SolicitudCompraService solicitudCompraService) {
        this.solicitudCompraService = solicitudCompraService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public List<SolicitudCompra> listarTodas() {
        return solicitudCompraService.listarTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitudCompra> listarPorUsuario(@PathVariable Long usuarioId) {
        return solicitudCompraService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/{id}")
    public SolicitudCompra buscarPorId(@PathVariable Long id) {
        return solicitudCompraService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ALMACEN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudCompra crear(@RequestBody SolicitudCompraRequest request) {
        return solicitudCompraService.crear(request);
    }

    @PutMapping("/{id}/revisar")
    @PreAuthorize("hasRole('GERENTE')")
    public SolicitudCompra revisar(@PathVariable Long id, @RequestBody RevisionSolicitudRequest request) {
        return solicitudCompraService.revisar(id, request);
    }
}