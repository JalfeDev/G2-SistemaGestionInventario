package com.g2.demo.service;

import com.g2.demo.entity.DetalleSolicitud;
import com.g2.demo.repository.DetalleSolicitudRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleSolicitudService extends CrudService<DetalleSolicitud> {

    public DetalleSolicitudService(DetalleSolicitudRepository repository) {
        super(repository, "Detalle de solicitud");
    }
}
