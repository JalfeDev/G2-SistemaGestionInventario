package com.g2.demo.repository;

import com.g2.demo.entity.DetalleDistribucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleDistribucionRepository extends JpaRepository<DetalleDistribucion, Long> {

    List<DetalleDistribucion> findAllByOrderByDistribucionFechaDesc();
}
