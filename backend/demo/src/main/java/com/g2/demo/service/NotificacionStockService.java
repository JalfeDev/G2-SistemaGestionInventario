package com.g2.demo.service;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.NotificacionStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class NotificacionStockService {

    public static final String TIPO_STOCK_CRITICO = "STOCK_CRITICO";

    private static final Logger log = LoggerFactory.getLogger(NotificacionStockService.class);

    private final NotificacionStockRepository repository;
    private final JavaMailSender mailSender;
    private final boolean envioHabilitado;
    private final String emailEncargado;
    private final String emailGerente;
    private final String frontendUrl;

    public NotificacionStockService(NotificacionStockRepository repository,
                                    JavaMailSender mailSender,
                                    @Value("${app.notifications.stock.enabled:false}") boolean envioHabilitado,
                                    @Value("${app.notifications.stock.email-encargado:}") String emailEncargado,
                                    @Value("${app.notifications.stock.email-gerente:}") String emailGerente,
                                    @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.envioHabilitado = envioHabilitado;
        this.emailEncargado = emailEncargado;
        this.emailGerente = emailGerente;
        this.frontendUrl = frontendUrl;
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
                log.info("Stock critico ya notificado para producto {}", producto.getNombre());
                return;
            }

            NotificacionStock notificacion = crearNotificacion(producto);
            enviarCorreo(notificacion);
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

    private void enviarCorreo(NotificacionStock notificacion) {
        if (!envioHabilitado) {
            notificacion.setMensajeError("Envio de correo desactivado por configuracion.");
            log.info("Stock critico detectado para {}. Envio de correo desactivado.",
                    notificacion.getProducto().getNombre());
            return;
        }
        String[] destinatarios = Stream.of(emailEncargado, emailGerente)
                .filter(email -> email != null && !email.isBlank())
                .toArray(String[]::new);
        if (destinatarios.length == 0) {
            notificacion.setMensajeError("Destinatario de correo no configurado.");
            log.warn("Stock critico detectado, pero no hay email destino configurado.");
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatarios);
            mensaje.setSubject("Alerta de stock critico - " + notificacion.getProducto().getNombre());
            mensaje.setText(construirMensaje(notificacion));
            mailSender.send(mensaje);
            notificacion.setEnviado(true);
            notificacion.setMensajeError(null);
        } catch (Exception ex) {
            notificacion.setEnviado(false);
            notificacion.setMensajeError(ex.getMessage());
            log.warn("No se pudo enviar correo de stock critico para producto {}",
                    notificacion.getProducto().getNombre(), ex);
        }
    }

    private String construirMensaje(NotificacionStock notificacion) {
        Producto producto = notificacion.getProducto();
        String categoria = producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoria";
        return "Reporte automatico de stock critico\n\n"
                + "Producto: " + producto.getNombre() + "\n"
                + "Categoria: " + categoria + "\n"
                + "Stock actual: " + notificacion.getStockActual() + "\n"
                + "Stock minimo: " + notificacion.getStockMinimo() + "\n"
                + "Fecha/hora de deteccion: " + notificacion.getFechaEnvio() + "\n\n"
                + "El producto requiere revision o reposicion."
                + "\n\nRevisa y gestiona la reposicion aqui: " + frontendUrl + "/solicitudes";
    }
}
