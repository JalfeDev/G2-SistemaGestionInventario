package com.g2.demo.service;

import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.repository.SolicitudCompraRepository;
import org.springframework.stereotype.Service;

@Service
public class SolicitudCompraService extends CrudService<SolicitudCompra> {

    public SolicitudCompraService(SolicitudCompraRepository repository) {
        super(repository, "Solicitud de compra");
    }
}
