package com.g2.demo.repository;

import com.g2.demo.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    boolean existsByProductoId(Long productoId);

    List<MovimientoInventario> findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
            String tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFinExclusiva);

    List<MovimientoInventario> findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(
            LocalDateTime fechaInicio, LocalDateTime fechaFinExclusiva);
}
