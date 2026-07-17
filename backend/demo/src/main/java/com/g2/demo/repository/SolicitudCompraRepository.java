package com.g2.demo.repository;

import com.g2.demo.entity.SolicitudCompra;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SolicitudCompraRepository extends JpaRepository<SolicitudCompra, Long> {
    List<SolicitudCompra> findBySolicitanteIdOrderByFechaSolicitudDesc(Long solicitanteId);
    List<SolicitudCompra> findAllByOrderByFechaSolicitudDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SolicitudCompra s where s.id = :id")
    Optional<SolicitudCompra> findWithLockById(@Param("id") Long id);
}
