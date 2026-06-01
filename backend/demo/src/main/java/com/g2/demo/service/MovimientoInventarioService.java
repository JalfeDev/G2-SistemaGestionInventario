package com.g2.demo.service;

import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;

@Service
public class MovimientoInventarioService extends CrudService<MovimientoInventario> {

    public MovimientoInventarioService(MovimientoInventarioRepository repository) {
        super(repository, "Movimiento de inventario");
    }
}
