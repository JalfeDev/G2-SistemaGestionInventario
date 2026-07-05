package com.g2.demo.dto;

import com.g2.demo.entity.DetalleIngreso;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//HU - Historial de precios por proveedor
@Getter
public class HistorialPrecioItem {

    private final LocalDateTime fecha;
    private final String producto;
    private final String proveedor;
    private final BigDecimal cantidad;
    private final BigDecimal costoUnitario;
    private final BigDecimal costoTotal;

    public HistorialPrecioItem(DetalleIngreso detalle) {
        this.fecha = detalle.getIngresoInventario().getFechaIngreso();
        this.producto = detalle.getProducto().getNombre();
        this.proveedor = detalle.getProveedor().getNombre();
        this.cantidad = detalle.getCantidad();
        this.costoUnitario = detalle.getCostoUnitario();
        this.costoTotal = detalle.getCostoTotal();
    }
}
