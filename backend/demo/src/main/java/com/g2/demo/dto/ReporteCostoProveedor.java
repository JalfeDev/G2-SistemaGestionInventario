package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

//HU - Reporte de costos por proveedor
@Getter
@AllArgsConstructor
public class ReporteCostoProveedor {

    private String proveedor;
    private List<ReporteCostoProveedorItem> items;
    private BigDecimal subtotal;
}
