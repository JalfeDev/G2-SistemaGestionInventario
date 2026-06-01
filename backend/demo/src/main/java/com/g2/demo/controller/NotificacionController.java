package com.g2.demo.controller;

import com.g2.demo.entity.Notificacion;
import com.g2.demo.service.NotificacionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController extends CrudController<Notificacion> {

    public NotificacionController(NotificacionService service) {
        super(service);
    }
}
