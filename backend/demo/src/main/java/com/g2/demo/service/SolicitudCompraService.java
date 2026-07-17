package com.g2.demo.service;

import com.g2.demo.dto.RevisionSolicitudRequest;
import com.g2.demo.dto.SolicitudCompraRequest;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.entity.Usuario;
import com.g2.demo.facade.SolicitudCompraFacade;
import com.g2.demo.repository.SolicitudCompraRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudCompraService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudCompraService.class);

    private final SolicitudCompraRepository solicitudCompraRepository;
    private final SolicitudCompraFacade solicitudCompraFacade;
    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final boolean envioHabilitado;
    private final String emailEncargado;

    public SolicitudCompraService(SolicitudCompraRepository solicitudCompraRepository,
                                   SolicitudCompraFacade solicitudCompraFacade,
                                   UsuarioRepository usuarioRepository,
                                   JavaMailSender mailSender,
                                   @Value("${app.notifications.solicitud.enabled:false}") boolean envioHabilitado,
                                   @Value("${app.notifications.solicitud.email-encargado:}") String emailEncargado) {
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.solicitudCompraFacade = solicitudCompraFacade;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.envioHabilitado = envioHabilitado;
        this.emailEncargado = emailEncargado;
    }

    public List<SolicitudCompra> listarTodas() {
        return solicitudCompraRepository.findAllByOrderByFechaSolicitudDesc();
    }

    public List<SolicitudCompra> listarPorUsuario(Long usuarioId) {
        return solicitudCompraRepository.findBySolicitanteIdOrderByFechaSolicitudDesc(usuarioId);
    }

    public SolicitudCompra buscarPorId(Long id) {
        return solicitudCompraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
    }

    public SolicitudCompra crear(SolicitudCompraRequest request) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud debe tener al menos un producto");
        }
        for (var detalle : request.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
            }
        }
        return solicitudCompraFacade.registrarSolicitud(request);
    }

    public SolicitudCompra revisar(Long id, RevisionSolicitudRequest request) {
        SolicitudCompra solicitud = buscarPorId(id);

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue revisada");
        }
        if (!"APROBADO".equals(request.getEstado()) && !"RECHAZADO".equals(request.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado invalido, use APROBADO o RECHAZADO");
        }
        if ("RECHAZADO".equals(request.getEstado()) &&
                (request.getComentario() == null || request.getComentario().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el motivo del rechazo");
        }

        Usuario aprobador = usuarioRepository.findById(request.getAprobadorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aprobador no encontrado"));

        solicitud.setEstado(request.getEstado());
        solicitud.setAprobador(aprobador);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        if (request.getComentario() != null && !request.getComentario().isBlank()) {
            solicitud.setComentario(request.getComentario());
        }

        SolicitudCompra guardada = solicitudCompraRepository.save(solicitud);
        enviarCorreoResolucion(guardada);
        return guardada;
    }

    private void enviarCorreoResolucion(SolicitudCompra solicitud) {
        if (!envioHabilitado) {
            return;
        }
        Usuario solicitante = solicitud.getSolicitante();
        if (solicitante == null || solicitante.getEmail() == null || solicitante.getEmail().isBlank()) {
            log.warn("No se pudo notificar la resolucion de la solicitud {}: el solicitante no tiene correo configurado", solicitud.getId());
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(solicitante.getEmail());
            if (emailEncargado != null && !emailEncargado.isBlank()) {
                mensaje.setCc(emailEncargado);
            }
            mensaje.setSubject("Solicitud de reabastecimiento #" + solicitud.getId() + " - " + solicitud.getEstado());
            mensaje.setText(construirMensajeResolucion(solicitud));
            mailSender.send(mensaje);
        } catch (Exception ex) {
            log.warn("No se pudo enviar el correo de resolucion de la solicitud {}", solicitud.getId(), ex);
        }
    }

    private String construirMensajeResolucion(SolicitudCompra solicitud) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Tu solicitud de reabastecimiento #").append(solicitud.getId())
                .append(" fue ").append(solicitud.getEstado()).append(".\n\n");
        if ("RECHAZADO".equals(solicitud.getEstado())) {
            mensaje.append("Motivo: ").append(solicitud.getComentario()).append("\n");
        }
        return mensaje.toString();
    }
}