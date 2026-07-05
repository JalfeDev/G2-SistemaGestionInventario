package com.g2.demo.service;

import com.g2.demo.entity.Notificacion;
import com.g2.demo.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService extends CrudService<Notificacion> {

    public NotificacionService(NotificacionRepository repository) {
        super(repository, "Notificacion");
    }
}
