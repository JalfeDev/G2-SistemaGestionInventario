package com.g2.demo.service;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.NotificacionStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionStockService {

    public static final String TIPO_STOCK_CRITICO = "STOCK_CRITICO";

    private static final Logger log = LoggerFactory.getLogger(NotificacionStockService.class);

    private final NotificacionStockRepository repository;

    public NotificacionStockService(NotificacionStockRepository repository) {
        this.repository = repository;
    }

    public List<NotificacionStock> listar() {
        return repository.findAllByOrderByFechaEnvioDesc();
    }

    public List<NotificacionStock> listarUltimas() {
        return repository.findTop10ByOrderByFechaEnvioDesc();
    }

    @Transactional
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
                log.info("Stock critico ya notificado para producto {}", producto.getNombre());
                return;
            }

            NotificacionStock notificacion = crearNotificacion(producto);
            repository.save(notificacion);
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
}
