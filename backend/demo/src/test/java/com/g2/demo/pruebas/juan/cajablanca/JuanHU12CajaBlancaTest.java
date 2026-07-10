package com.g2.demo.pruebas.juan.cajablanca;

import com.g2.demo.controller.DashboardController;
import com.g2.demo.dto.DashboardDTO;
import com.g2.demo.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuanHU12CajaBlancaTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void obtenerDashboard_delegaEnServicioYRetornaMismoDto() {
        DashboardDTO dashboardEsperado = new DashboardDTO(
                LocalDateTime.of(2026, 7, 9, 22, 30),
                new DashboardDTO.Resumen(5, 2, 1, 8, 3),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
        when(dashboardService.obtenerDashboard()).thenReturn(dashboardEsperado);

        DashboardDTO resultado = dashboardController.obtenerDashboard();

        assertSame(dashboardEsperado, resultado);
        verify(dashboardService).obtenerDashboard();
    }

    @Test
    void obtenerDashboard_exponeEndpointDashboardSoloParaGerente() throws NoSuchMethodException {
        Method metodo = DashboardController.class.getDeclaredMethod("obtenerDashboard");

        GetMapping getMapping = metodo.getAnnotation(GetMapping.class);
        PreAuthorize preAuthorize = metodo.getAnnotation(PreAuthorize.class);

        assertArrayEquals(new String[]{"/api/dashboard"}, getMapping.value());
        assertEquals("hasRole('GERENTE')", preAuthorize.value());
    }
}
