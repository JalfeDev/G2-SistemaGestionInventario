package com.g2.demo.repository;

import com.g2.demo.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    Optional<UnidadMedida> findByNombreIgnoreCase(String nombre);
}
