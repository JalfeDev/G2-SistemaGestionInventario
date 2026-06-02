package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReporteConsumoResponse {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<ReporteConsumoCategoria> categorias;
    private BigDecimal totalGeneral;
}
