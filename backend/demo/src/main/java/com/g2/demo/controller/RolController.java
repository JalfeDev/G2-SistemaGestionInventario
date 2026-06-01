package com.g2.demo.controller;

import com.g2.demo.entity.Rol;
import com.g2.demo.service.RolService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RolController extends CrudController<Rol> {

    public RolController(RolService service) {
        super(service);
    }
}
