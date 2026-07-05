package com.g2.demo.service;

import com.g2.demo.entity.Proveedor;
import com.g2.demo.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService extends CrudService<Proveedor> {

    public ProveedorService(ProveedorRepository proveedorRepository) {
        super(proveedorRepository, "Proveedor");
    }
}
