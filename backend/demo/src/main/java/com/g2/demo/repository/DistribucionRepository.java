package com.g2.demo.repository;

import com.g2.demo.entity.Distribucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
    List<Distribucion> findByHabitacionId(Long habitacionId);
    List<Distribucion> findByFechaAsignacionBetween(LocalDateTime inicio, LocalDateTime fin);
}