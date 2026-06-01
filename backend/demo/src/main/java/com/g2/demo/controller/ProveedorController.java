package com.g2.demo.controller;

import com.g2.demo.entity.Proveedor;
import com.g2.demo.service.ProveedorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController extends CrudController<Proveedor> {

    public ProveedorController(ProveedorService proveedorService) {
        super(proveedorService);
    }
}
