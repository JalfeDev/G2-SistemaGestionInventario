package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReporteConsumoItem {

    private Long productoId;
    private String producto;
    private String categoria;
    private BigDecimal cantidadConsumida;
}
