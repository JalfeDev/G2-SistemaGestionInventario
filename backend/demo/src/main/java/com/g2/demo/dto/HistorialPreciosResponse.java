package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

//HU -Historial de precios por proveedor
@Getter
@Setter
public class HistorialPreciosResponse {
    private List<HistorialPrecioItem> historial;
    private BigDecimal precioPromedio;
}