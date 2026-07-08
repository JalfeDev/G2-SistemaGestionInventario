package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class DetalleSolicitudRequest {
    private Long productoId;
    private BigDecimal cantidad;
}