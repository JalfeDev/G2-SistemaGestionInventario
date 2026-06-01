package com.g2.demo.service;

import com.g2.demo.entity.DistribucionInsumos;
import com.g2.demo.repository.DistribucionInsumosRepository;
import org.springframework.stereotype.Service;

@Service
public class DistribucionInsumosService extends CrudService<DistribucionInsumos> {

    public DistribucionInsumosService(DistribucionInsumosRepository repository) {
        super(repository, "Distribucion de insumos");
    }
}
