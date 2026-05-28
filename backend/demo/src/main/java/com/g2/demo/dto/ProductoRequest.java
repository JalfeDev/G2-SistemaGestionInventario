package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {
    private String nombre;
    private String codigo;
    private String descripcion;
    private Integer stockMinimo;
    private BigDecimal precioUnitario;
    private Long categoriaId;
    private Long proveedorId;
}