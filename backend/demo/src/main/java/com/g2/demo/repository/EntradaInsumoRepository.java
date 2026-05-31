package com.g2.demo.repository;

import com.g2.demo.entity.EntradaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntradaInsumoRepository extends JpaRepository<EntradaInsumo, Long> {
}