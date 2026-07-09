package com.g2.demo.controller;

import com.g2.demo.dto.DashboardDTO;
import com.g2.demo.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard")
    @PreAuthorize("hasRole('GERENTE')")
    public DashboardDTO obtenerDashboard() {
        return dashboardService.obtenerDashboard();
    }
}
