package com.g2.demo.repository;

import com.g2.demo.entity.NotificacionStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionStockRepository extends JpaRepository<NotificacionStock, Long> {

    List<NotificacionStock> findByProductoIdAndTipoAndResueltaFalse(Long productoId, String tipo);

    List<NotificacionStock> findAllByOrderByFechaEnvioDesc();

    List<NotificacionStock> findTop10ByOrderByFechaEnvioDesc();
}
