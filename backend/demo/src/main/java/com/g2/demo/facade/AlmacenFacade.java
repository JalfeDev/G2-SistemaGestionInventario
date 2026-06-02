package com.g2.demo.facade;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.service.IngresoInventarioService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlmacenFacade {

    private final IngresoInventarioService ingresoInventarioService;

    public AlmacenFacade(IngresoInventarioService ingresoInventarioService) {
        this.ingresoInventarioService = ingresoInventarioService;
    }

    public List<DetalleIngreso> listarHistorial() {
        return ingresoInventarioService.listarHistorial();
    }

    // Simplifica el acceso del controller al caso de uso que coordina entradas, stock y movimientos.
    public DetalleIngreso registrarEntrada(RegistrarEntradaRequest request, String username) {
        return ingresoInventarioService.registrar(request, username);
    }
}
