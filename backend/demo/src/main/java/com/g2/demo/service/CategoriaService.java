package com.g2.demo.service;

import com.g2.demo.entity.Categoria;
import com.g2.demo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService extends CrudService<Categoria> {

    public CategoriaService(CategoriaRepository categoriaRepository) {
        super(categoriaRepository, "Categoria");
    }
}
