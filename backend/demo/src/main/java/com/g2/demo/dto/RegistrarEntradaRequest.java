package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegistrarEntradaRequest {

    private Long productoId;
    private Long proveedorId;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private String observacion;
    private Long solicitudId;
}
