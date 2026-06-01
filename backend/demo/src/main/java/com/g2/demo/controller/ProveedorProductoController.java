package com.g2.demo.controller;

import com.g2.demo.entity.ProveedorProducto;
import com.g2.demo.service.ProveedorProductoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedor-producto")
public class ProveedorProductoController extends CrudController<ProveedorProducto> {

    public ProveedorProductoController(ProveedorProductoService service) {
        super(service);
    }
}
