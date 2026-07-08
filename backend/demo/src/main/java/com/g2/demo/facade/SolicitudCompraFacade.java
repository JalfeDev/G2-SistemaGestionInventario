package com.g2.demo.facade;

import com.g2.demo.dto.DetalleSolicitudRequest;
import com.g2.demo.dto.SolicitudCompraRequest;
import com.g2.demo.entity.*;
import com.g2.demo.repository.DetalleSolicitudRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.SolicitudCompraRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Component
public class SolicitudCompraFacade {

    private final SolicitudCompraRepository solicitudCompraRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public SolicitudCompraFacade(SolicitudCompraRepository solicitudCompraRepository,
                                  DetalleSolicitudRepository detalleSolicitudRepository,
                                  UsuarioRepository usuarioRepository,
                                  ProductoRepository productoRepository) {
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public SolicitudCompra registrarSolicitud(SolicitudCompraRequest request) {
        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitante no encontrado"));

        SolicitudCompra cabecera = new SolicitudCompra();
        cabecera.setFechaSolicitud(LocalDateTime.now());
        cabecera.setEstado("PENDIENTE");
        cabecera.setComentario(request.getComentario());
        cabecera.setSolicitante(solicitante);
        SolicitudCompra guardada = solicitudCompraRepository.save(cabecera);

        for (DetalleSolicitudRequest detalleReq : request.getDetalles()) {
            Producto producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            DetalleSolicitud detalle = new DetalleSolicitud();
            detalle.setSolicitud(guardada);
            detalle.setProducto(producto);
            detalle.setCantidadSolicitada(detalleReq.getCantidad());
            detalleSolicitudRepository.save(detalle);
        }

        return guardada;
    }
}