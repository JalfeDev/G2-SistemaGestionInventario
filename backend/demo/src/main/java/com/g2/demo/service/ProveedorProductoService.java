package com.g2.demo.service;

import com.g2.demo.entity.ProveedorProducto;
import com.g2.demo.repository.ProveedorProductoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProveedorProductoService extends CrudService<ProveedorProducto> {

    public ProveedorProductoService(ProveedorProductoRepository repository) {
        super(repository, "Relacion proveedor-producto");
    }
}
