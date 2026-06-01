package com.g2.demo.controller;

import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.service.MovimientoInventarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController extends CrudController<MovimientoInventario> {

    public MovimientoInventarioController(MovimientoInventarioService service) {
        super(service);
    }
}
