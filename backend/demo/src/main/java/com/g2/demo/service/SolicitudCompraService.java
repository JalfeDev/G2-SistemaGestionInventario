package com.g2.demo.service;

import com.g2.demo.dto.RevisionSolicitudRequest;
import com.g2.demo.dto.SolicitudCompraRequest;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.entity.Usuario;
import com.g2.demo.facade.SolicitudCompraFacade;
import com.g2.demo.repository.SolicitudCompraRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudCompraService {

    private final SolicitudCompraRepository solicitudCompraRepository;
    private final SolicitudCompraFacade solicitudCompraFacade;
    private final UsuarioRepository usuarioRepository;

    public SolicitudCompraService(SolicitudCompraRepository solicitudCompraRepository,
                                   SolicitudCompraFacade solicitudCompraFacade,
                                   UsuarioRepository usuarioRepository) {
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.solicitudCompraFacade = solicitudCompraFacade;
        this.usuarioRepository = usuarioRepository;
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

        return solicitudCompraRepository.save(solicitud);
    }
}