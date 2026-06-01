package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class EntradaInsumoRequest {
    private Long productoId;
    private Long proveedorId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}