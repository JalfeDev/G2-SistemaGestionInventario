package com.g2.demo.repository;

import com.g2.demo.entity.SolicitudCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudCompraRepository extends JpaRepository<SolicitudCompra, Long> {
    List<SolicitudCompra> findBySolicitanteIdOrderByFechaSolicitudDesc(Long solicitanteId);
    List<SolicitudCompra> findAllByOrderByFechaSolicitudDesc();
}
