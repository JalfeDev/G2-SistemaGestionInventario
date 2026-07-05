package com.g2.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

public class CrudService<T> {

    private final JpaRepository<T, Long> repository;
    private final String entityName;

    protected CrudService(JpaRepository<T, Long> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public List<T> listar() {
        return repository.findAll();
    }

    public T buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + " no encontrado"));
    }

    public T crear(T entity) {
        setId(entity, null);
        return repository.save(entity);
    }

    public T actualizar(Long id, T entity) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + " no encontrado");
        }
        setId(entity, id);
        return repository.save(entity);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + " no encontrado");
        }
        repository.deleteById(id);
    }

    private void setId(T entity, Long id) {
        Field field = ReflectionUtils.findField(entity.getClass(), "id");
        if (field == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "La entidad no tiene campo id");
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, entity, id);
    }
}
