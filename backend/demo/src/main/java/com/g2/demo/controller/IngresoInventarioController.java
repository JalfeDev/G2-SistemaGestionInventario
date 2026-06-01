package com.g2.demo.controller;

import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.service.IngresoInventarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingresos-inventario")
public class IngresoInventarioController extends CrudController<IngresoInventario> {

    public IngresoInventarioController(IngresoInventarioService service) {
        super(service);
    }
}
