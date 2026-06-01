package com.g2.demo.service;

import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.repository.DetalleDistribucionRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleDistribucionService extends CrudService<DetalleDistribucion> {

    public DetalleDistribucionService(DetalleDistribucionRepository repository) {
        super(repository, "Detalle de distribucion");
    }
}
