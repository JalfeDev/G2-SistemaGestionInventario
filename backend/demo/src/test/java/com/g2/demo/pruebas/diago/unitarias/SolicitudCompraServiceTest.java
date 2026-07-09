package com.g2.demo.pruebas.diago.unitarias;

import com.g2.demo.dto.DetalleSolicitudRequest;
import com.g2.demo.dto.RevisionSolicitudRequest;
import com.g2.demo.dto.SolicitudCompraRequest;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.entity.Usuario;
import com.g2.demo.facade.SolicitudCompraFacade;
import com.g2.demo.repository.SolicitudCompraRepository;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.SolicitudCompraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudCompraServiceTest {

    @Mock private SolicitudCompraRepository solicitudCompraRepository;
    @Mock private SolicitudCompraFacade solicitudCompraFacade;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitudCompraService solicitudCompraService;

    // 1. crear con detalles válidos delega al Facade
    @Test
    void crear_conDetallesValidos_delegaAlFacadeYRetornaPendiente() {
        SolicitudCompraRequest request = new SolicitudCompraRequest();
        request.setSolicitanteId(1L);
        request.setComentario("Faltan toallas en almacen");
        DetalleSolicitudRequest detalle = new DetalleSolicitudRequest();
        detalle.setProductoId(5L);
        detalle.setCantidad(new BigDecimal("10"));
        request.setDetalles(List.of(detalle));

        SolicitudCompra creada = new SolicitudCompra();
        creada.setEstado("PENDIENTE");
        when(solicitudCompraFacade.registrarSolicitud(request)).thenReturn(creada);

        SolicitudCompra resultado = solicitudCompraService.crear(request);

        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
        verify(solicitudCompraFacade, times(1)).registrarSolicitud(request);
    }

    // 2. sin detalles no se puede crear
    @Test
    void crear_sinDetalles_lanzaBadRequestYNoLlamaAlFacade() {
        SolicitudCompraRequest request = new SolicitudCompraRequest();
        request.setSolicitanteId(1L);
        request.setDetalles(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> solicitudCompraService.crear(request));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(solicitudCompraFacade, never()).registrarSolicitud(any());
    }

    // 3. el gerente aprueba una solicitud pendiente
    @Test
    void revisar_aprobarSolicitudPendiente_actualizaEstadoYAprobador() {
        SolicitudCompra pendiente = new SolicitudCompra();
        pendiente.setId(1L);
        pendiente.setEstado("PENDIENTE");

        Usuario gerente = new Usuario();
        gerente.setId(2L);

        when(solicitudCompraRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(gerente));
        when(solicitudCompraRepository.save(any(SolicitudCompra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RevisionSolicitudRequest request = new RevisionSolicitudRequest();
        request.setAprobadorId(2L);
        request.setEstado("APROBADO");

        SolicitudCompra resultado = solicitudCompraService.revisar(1L, request);

        assertThat(resultado.getEstado()).isEqualTo("APROBADO");
        assertThat(resultado.getAprobador()).isEqualTo(gerente);
        assertThat(resultado.getFechaRespuesta()).isNotNull();
    }

    // 4. rechazar sin comentario no está permitido
    @Test
    void revisar_rechazarSinComentario_lanzaBadRequest() {
        SolicitudCompra pendiente = new SolicitudCompra();
        pendiente.setId(1L);
        pendiente.setEstado("PENDIENTE");

        when(solicitudCompraRepository.findById(1L)).thenReturn(Optional.of(pendiente));

        RevisionSolicitudRequest request = new RevisionSolicitudRequest();
        request.setAprobadorId(2L);
        request.setEstado("RECHAZADO");
        request.setComentario(null); // falta el motivo

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> solicitudCompraService.revisar(1L, request));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(solicitudCompraRepository, never()).save(any());
    }
}