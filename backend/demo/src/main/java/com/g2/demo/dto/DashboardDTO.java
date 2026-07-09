package com.g2.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardDTO {

    private LocalDateTime generadoEn;
    private Resumen resumen;
    private List<StockCategoria> stockPorCategoria;
    private List<SolicitudesEstado> solicitudesPorEstado;
    private List<MovimientosTipo> movimientosPorTipo;
    private List<ProductoStockBajo> productosStockBajo;
    private List<MovimientoReciente> movimientosRecientes;

    @Getter
    @AllArgsConstructor
    public static class Resumen {
        private long totalProductos;
        private long productosStockBajo;
        private long solicitudesPendientes;
        private long movimientosRegistrados;
        private long notificacionesStockActivas;
    }

    @Getter
    @AllArgsConstructor
    public static class StockCategoria {
        private String categoria;
        private BigDecimal stockTotal;
        private long productos;
    }

    @Getter
    @AllArgsConstructor
    public static class SolicitudesEstado {
        private String estado;
        private long total;
    }

    @Getter
    @AllArgsConstructor
    public static class MovimientosTipo {
        private String tipoMovimiento;
        private long totalMovimientos;
        private BigDecimal cantidadTotal;
    }

    @Getter
    @AllArgsConstructor
    public static class ProductoStockBajo {
        private Long idProducto;
        private String nombre;
        private String categoria;
        private String unidad;
        private BigDecimal stockActual;
        private BigDecimal stockMinimo;
    }

    @Getter
    @AllArgsConstructor
    public static class MovimientoReciente {
        private Long idMovimiento;
        private String tipoMovimiento;
        private String producto;
        private BigDecimal cantidad;
        private BigDecimal stockAnterior;
        private BigDecimal stockNuevo;
        private LocalDateTime fechaMovimiento;
    }
}
