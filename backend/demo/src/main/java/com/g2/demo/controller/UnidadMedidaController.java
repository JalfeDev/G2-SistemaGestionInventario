package com.g2.demo.controller;

import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.service.UnidadMedidaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unidades-medida")
public class UnidadMedidaController extends CrudController<UnidadMedida> {

    public UnidadMedidaController(UnidadMedidaService service) {
        super(service);
    }
}
