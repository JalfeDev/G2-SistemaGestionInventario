package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SolicitudCompraRequest {
    private Long solicitanteId;
    private String comentario; // motivo de la solicitud
    private List<DetalleSolicitudRequest> detalles;
}