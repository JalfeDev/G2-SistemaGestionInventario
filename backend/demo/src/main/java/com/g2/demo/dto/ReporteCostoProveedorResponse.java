package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//HU - Reporte de costos por proveedor
@Getter
@AllArgsConstructor
public class ReporteCostoProveedorResponse {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<ReporteCostoProveedor> proveedores;
    private BigDecimal totalGeneral;
}
