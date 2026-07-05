package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {
    private String nombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private Long categoriaId;
    private Long unidadId;
}
