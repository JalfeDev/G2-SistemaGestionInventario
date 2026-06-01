package com.g2.demo.controller;

import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.service.SolicitudCompraService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/solicitudes-compra")
public class SolicitudCompraController extends CrudController<SolicitudCompra> {

    public SolicitudCompraController(SolicitudCompraService service) {
        super(service);
    }
}
