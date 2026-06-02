package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegistrarDistribucionRequest {

    private Long productoId;
    private Long habitacionId;
    private BigDecimal cantidad;
}
