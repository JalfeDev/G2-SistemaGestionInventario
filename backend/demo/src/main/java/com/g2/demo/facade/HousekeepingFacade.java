package com.g2.demo.facade;

import com.g2.demo.dto.RegistrarDistribucionRequest;
import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.service.DistribucionInsumosService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class HousekeepingFacade {

    private final DistribucionInsumosService distribucionInsumosService;

    public HousekeepingFacade(DistribucionInsumosService distribucionInsumosService) {
        this.distribucionInsumosService = distribucionInsumosService;
    }

    public List<DetalleDistribucion> listarHistorial(LocalDate fechaInicio, LocalDate fechaFin, Long habitacionId) {
        return distribucionInsumosService.listarHistorial(fechaInicio, fechaFin, habitacionId);
    }

    // Simplifica el acceso del controller al caso de uso que coordina habitaciones, stock y movimientos.
    public DetalleDistribucion registrarDistribucion(RegistrarDistribucionRequest request, String username) {
        return distribucionInsumosService.registrar(request, username);
    }
}
