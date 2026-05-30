package com.g2.demo.service;

import com.g2.demo.entity.Proveedor;
import com.g2.demo.repository.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
    }

    public Proveedor crear(Proveedor proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor existente = buscarPorId(id);
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getRuc() != null) existente.setRuc(datos.getRuc());
        if (datos.getTelefono() != null) existente.setTelefono(datos.getTelefono());
        if (datos.getEmail() != null) existente.setEmail(datos.getEmail());
        if (datos.getDireccion() != null) existente.setDireccion(datos.getDireccion());
        return proveedorRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        proveedorRepository.deleteById(id);
    }
}