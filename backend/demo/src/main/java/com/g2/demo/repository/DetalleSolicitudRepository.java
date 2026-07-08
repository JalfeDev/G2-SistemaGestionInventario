package com.g2.demo.repository;

import com.g2.demo.entity.DetalleSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleSolicitudRepository extends JpaRepository<DetalleSolicitud, Long> {
    List<DetalleSolicitud> findBySolicitudId(Long solicitudId);
}
