package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevisionSolicitudRequest {
    private Long aprobadorId;
    private String estado;     // APROBADO o RECHAZADO
    private String comentario; // obligatorio si estado = RECHAZADO
}