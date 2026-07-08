package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReporteConsumoCategoria {

    private String categoria;
    private BigDecimal subtotal;
    private List<ReporteConsumoItem> productos;
}
