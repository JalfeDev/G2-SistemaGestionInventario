package com.g2.demo.service;

import com.g2.demo.entity.Rol;
import com.g2.demo.repository.RolRepository;
import org.springframework.stereotype.Service;

@Service
public class RolService extends CrudService<Rol> {

    public RolService(RolRepository repository) {
        super(repository, "Rol");
    }
}
