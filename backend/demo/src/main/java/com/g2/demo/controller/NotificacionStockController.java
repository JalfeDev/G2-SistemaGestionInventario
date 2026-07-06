package com.g2.demo.controller;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.facade.NotificacionStockFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones-stock")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
public class NotificacionStockController {

    private final NotificacionStockFacade facade;

    public NotificacionStockController(NotificacionStockFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<NotificacionStock> listar() {
        return facade.listar();
    }

    @GetMapping("/ultimas")
    public List<NotificacionStock> listarUltimas() {
        return facade.listarUltimas();
    }
}
