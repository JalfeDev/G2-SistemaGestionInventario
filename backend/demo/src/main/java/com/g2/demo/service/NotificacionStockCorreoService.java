package com.g2.demo.service;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.NotificacionStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class NotificacionStockCorreoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionStockCorreoService.class);

    private final NotificacionStockRepository repository;
    private final JavaMailSender mailSender;
    private final boolean envioHabilitado;
    private final String emailEncargado;
    private final String emailGerente;
    private final String frontendUrl;

    public NotificacionStockCorreoService(NotificacionStockRepository repository,
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

    @Async
    @Transactional
    public void enviarCorreo(Long notificacionId) {
        NotificacionStock notificacion = repository.findById(notificacionId).orElse(null);
        if (notificacion == null || Boolean.TRUE.equals(notificacion.getEnviado())) {
            return;
        }
        if (!envioHabilitado) {
            notificacion.setMensajeError("Envio de correo desactivado por configuracion.");
            repository.save(notificacion);
            return;
        }

        String[] destinatarios = Stream.of(emailEncargado, emailGerente)
                .filter(email -> email != null && !email.isBlank())
                .toArray(String[]::new);
        if (destinatarios.length == 0) {
            notificacion.setMensajeError("Destinatario de correo no configurado.");
            repository.save(notificacion);
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
        repository.save(notificacion);
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
