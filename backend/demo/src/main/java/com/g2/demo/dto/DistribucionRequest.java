package com.g2.demo.dto;

import lombok.Data;

@Data
public class DistribucionRequest {
    private Long productoId;
    private Long habitacionId;
    private Integer cantidad;
}
