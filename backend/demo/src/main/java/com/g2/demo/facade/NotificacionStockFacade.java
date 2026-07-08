package com.g2.demo.facade;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.service.NotificacionStockService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacionStockFacade {

    private final NotificacionStockService service;

    public NotificacionStockFacade(NotificacionStockService service) {
        this.service = service;
    }

    public List<NotificacionStock> listar() {
        return service.listar();
    }

    public List<NotificacionStock> listarUltimas() {
        return service.listarUltimas();
    }
}
