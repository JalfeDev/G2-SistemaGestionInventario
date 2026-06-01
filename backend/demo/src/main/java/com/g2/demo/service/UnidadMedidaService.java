package com.g2.demo.service;

import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.UnidadMedidaRepository;
import org.springframework.stereotype.Service;

@Service
public class UnidadMedidaService extends CrudService<UnidadMedida> {

    public UnidadMedidaService(UnidadMedidaRepository repository) {
        super(repository, "Unidad de medida");
    }
}
