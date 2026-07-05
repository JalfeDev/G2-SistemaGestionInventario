package com.g2.demo.service;

import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.UnidadMedidaRepository;
import com.g2.demo.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UnidadMedidaService extends CrudService<UnidadMedida> {

    private final ProductoRepository productoRepository;

    public UnidadMedidaService(UnidadMedidaRepository repository, ProductoRepository productoRepository) {
        super(repository, "Unidad de medida");
        this.productoRepository = productoRepository;
    }

    @Override
    public void eliminar(Long id) {
        if (productoRepository.existsByUnidadId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una unidad de medida con productos asignados");
        }
        super.eliminar(id);
    }
}
