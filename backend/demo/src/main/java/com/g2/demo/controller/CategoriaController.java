package com.g2.demo.controller;

import com.g2.demo.entity.Categoria;
import com.g2.demo.service.CategoriaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/categorias")
@PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
public class CategoriaController extends CrudController<Categoria> {

    public CategoriaController(CategoriaService categoriaService) {
        super(categoriaService);
    }
}
