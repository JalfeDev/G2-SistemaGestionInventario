package com.g2.demo.service;

import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.repository.IngresoInventarioRepository;
import org.springframework.stereotype.Service;

@Service
public class IngresoInventarioService extends CrudService<IngresoInventario> {

    public IngresoInventarioService(IngresoInventarioRepository repository) {
        super(repository, "Ingreso de inventario");
    }
}
