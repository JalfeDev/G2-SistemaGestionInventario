package com.g2.demo.service;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.NotificacionStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionStockService {

    public static final String TIPO_STOCK_CRITICO = "STOCK_CRITICO";

    private static final Logger log = LoggerFactory.getLogger(NotificacionStockService.class);

    private final NotificacionStockRepository repository;
    private final NotificacionStockCorreoService correoService;

    public NotificacionStockService(NotificacionStockRepository repository,
                                    NotificacionStockCorreoService correoService) {
        this.repository = repository;
        this.correoService = correoService;
    }

    public List<NotificacionStock> listar() {
        return repository.findAllByOrderByFechaEnvioDesc();
    }

    public List<NotificacionStock> listarUltimas() {
        return repository.findTop10ByOrderByFechaEnvioDesc();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void evaluarStockCritico(Producto producto) {
        try {
            if (producto == null || producto.getId() == null
                    || producto.getStockActual() == null || producto.getStockMinimo() == null) {
                return;
            }

            List<NotificacionStock> activas = repository.findByProductoIdAndTipoAndResueltaFalse(
                    producto.getId(), TIPO_STOCK_CRITICO);
            boolean critico = producto.getStockActual().compareTo(producto.getStockMinimo()) <= 0;

            if (!critico) {
                resolverNotificaciones(producto, activas);
                return;
            }
            if (!activas.isEmpty()) {
                activas.stream()
                        .filter(notificacion -> Boolean.FALSE.equals(notificacion.getEnviado()))
                        .findFirst()
                        .ifPresent(this::programarEnvioCorreo);
                log.info("Stock critico ya notificado para producto {}", producto.getNombre());
                return;
            }

            NotificacionStock notificacion = crearNotificacion(producto);
            repository.save(notificacion);
            programarEnvioCorreo(notificacion);
        } catch (Exception ex) {
            log.warn("No se pudo procesar la notificacion de stock critico", ex);
        }
    }

    private void resolverNotificaciones(Producto producto, List<NotificacionStock> activas) {
        if (activas.isEmpty()) {
            return;
        }
        activas.forEach(notificacion -> notificacion.setResuelta(true));
        repository.saveAll(activas);
        log.info("Stock recuperado para producto {}. Notificaciones activas marcadas como resueltas.",
                producto.getNombre());
    }

    private NotificacionStock crearNotificacion(Producto producto) {
        NotificacionStock notificacion = new NotificacionStock();
        notificacion.setProducto(producto);
        notificacion.setStockActual(producto.getStockActual());
        notificacion.setStockMinimo(producto.getStockMinimo());
        notificacion.setTipo(TIPO_STOCK_CRITICO);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEnviado(false);
        notificacion.setResuelta(false);
        return notificacion;
    }

    private void programarEnvioCorreo(NotificacionStock notificacion) {
        if (notificacion.getId() == null) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            correoService.enviarCorreo(notificacion.getId());
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                correoService.enviarCorreo(notificacion.getId());
            }
        });
    }
}
