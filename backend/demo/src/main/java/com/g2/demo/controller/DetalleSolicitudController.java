package com.g2.demo.controller;

import com.g2.demo.entity.DetalleSolicitud;
import com.g2.demo.service.DetalleSolicitudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-solicitud")
public class DetalleSolicitudController extends CrudController<DetalleSolicitud> {

    public DetalleSolicitudController(DetalleSolicitudService service) {
        super(service);
    }
}
