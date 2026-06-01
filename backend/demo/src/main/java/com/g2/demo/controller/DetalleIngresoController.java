package com.g2.demo.controller;

import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.service.DetalleIngresoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-ingreso")
public class DetalleIngresoController extends CrudController<DetalleIngreso> {

    public DetalleIngresoController(DetalleIngresoService service) {
        super(service);
    }
}
