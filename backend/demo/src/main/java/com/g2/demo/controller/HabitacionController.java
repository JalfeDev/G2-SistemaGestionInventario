package com.g2.demo.controller;

import com.g2.demo.entity.Habitacion;
import com.g2.demo.service.HabitacionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController extends CrudController<Habitacion> {

    public HabitacionController(HabitacionService service) {
        super(service);
    }
}
