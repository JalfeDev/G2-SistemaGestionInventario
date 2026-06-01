package com.g2.demo.controller;

import com.g2.demo.entity.DistribucionInsumos;
import com.g2.demo.service.DistribucionInsumosService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/distribuciones-insumos")
public class DistribucionInsumosController extends CrudController<DistribucionInsumos> {

    public DistribucionInsumosController(DistribucionInsumosService service) {
        super(service);
    }
}
