package com.g2.demo.service;

import com.g2.demo.entity.Categoria;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoriaService extends CrudService<Categoria> {

    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        super(categoriaRepository, "Categoria");
        this.productoRepository = productoRepository;
    }

    @Override
    public void eliminar(Long id) {
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una categoria con productos asignados");
        }
        super.eliminar(id);
    }
}
