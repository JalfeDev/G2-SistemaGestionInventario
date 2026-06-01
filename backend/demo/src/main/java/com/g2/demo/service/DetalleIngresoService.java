package com.g2.demo.service;

import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.repository.DetalleIngresoRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleIngresoService extends CrudService<DetalleIngreso> {

    public DetalleIngresoService(DetalleIngresoRepository repository) {
        super(repository, "Detalle de ingreso");
    }
}
