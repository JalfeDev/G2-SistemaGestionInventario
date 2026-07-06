package com.g2.demo.repository;

import com.g2.demo.entity.DetalleIngreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, Long> {
    //HU - Historial de precios por proveedor

    List<DetalleIngreso> findAllByOrderByIngresoInventarioFechaIngresoDesc();

    List<DetalleIngreso> findByProducto_IdAndProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(
            Long productoId, Long proveedorId);

    List<DetalleIngreso> findByProducto_IdOrderByIngresoInventarioFechaIngresoDesc(Long productoId);

    List<DetalleIngreso> findByProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(Long proveedorId);

    //HU - Reporte de costos por proveedor
    List<DetalleIngreso> findByProveedor_IdAndIngresoInventario_FechaIngresoGreaterThanEqualAndIngresoInventario_FechaIngresoLessThanOrderByIngresoInventario_FechaIngresoDesc(
            Long proveedorId, LocalDateTime desde, LocalDateTime hastaExclusiva);

    List<DetalleIngreso> findByIngresoInventario_FechaIngresoGreaterThanEqualAndIngresoInventario_FechaIngresoLessThanOrderByIngresoInventario_FechaIngresoDesc(
            LocalDateTime desde, LocalDateTime hastaExclusiva);
}
