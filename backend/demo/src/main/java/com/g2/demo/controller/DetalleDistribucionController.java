package com.g2.demo.controller;

import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.service.DetalleDistribucionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-distribucion")
public class DetalleDistribucionController extends CrudController<DetalleDistribucion> {

    public DetalleDistribucionController(DetalleDistribucionService service) {
        super(service);
    }
}
