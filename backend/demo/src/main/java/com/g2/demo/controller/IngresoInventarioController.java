package com.g2.demo.controller;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.dto.HistorialPreciosResponse;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.facade.AlmacenFacade;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ingresos-inventario")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR','ALMACEN')")

public class IngresoInventarioController {

    private final AlmacenFacade facade;

    public IngresoInventarioController(AlmacenFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<DetalleIngreso> listar() {
        return facade.listarHistorial();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleIngreso registrar(@RequestBody RegistrarEntradaRequest request, Principal principal) {
        return facade.registrarEntrada(request, principal.getName());
    }

    //HU - Historial de precios por proveedor
    @GetMapping("/historial-precios")
    public HistorialPreciosResponse consultarHistorialPrecios(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long proveedorId) {
        return facade.consultarHistorialPrecios(productoId, proveedorId);
    }
}
