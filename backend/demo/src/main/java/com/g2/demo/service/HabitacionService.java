package com.g2.demo.service;

import com.g2.demo.entity.Habitacion;
import com.g2.demo.repository.HabitacionRepository;
import org.springframework.stereotype.Service;

@Service
public class HabitacionService extends CrudService<Habitacion> {

    public HabitacionService(HabitacionRepository repository) {
        super(repository, "Habitacion");
    }
}
