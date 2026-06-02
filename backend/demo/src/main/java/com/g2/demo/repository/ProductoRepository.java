package com.g2.demo.repository;

import com.g2.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);

    boolean existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(String nombre, Long categoriaId, Long id);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByUnidadId(Long unidadId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Producto> findWithLockById(Long id);
}
