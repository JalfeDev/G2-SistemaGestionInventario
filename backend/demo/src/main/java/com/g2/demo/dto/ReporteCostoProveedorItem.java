package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//HU - Reporte de costos por proveedor
@Getter
@AllArgsConstructor
public class ReporteCostoProveedorItem {

    private LocalDateTime fecha;
    private String producto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal costoTotal;
}
