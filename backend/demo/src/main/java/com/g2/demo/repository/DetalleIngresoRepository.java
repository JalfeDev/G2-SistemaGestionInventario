package com.g2.demo.repository;

import com.g2.demo.entity.DetalleIngreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, Long> {

    List<DetalleIngreso> findAllByOrderByIngresoInventarioFechaIngresoDesc();
}
