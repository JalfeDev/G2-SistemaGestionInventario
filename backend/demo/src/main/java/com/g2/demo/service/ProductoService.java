package com.g2.demo.service;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           UnidadMedidaRepository unidadMedidaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public List<Producto> listarAlertas() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual().compareTo(p.getStockMinimo()) <= 0)
                .toList();
    }

    public Producto crear(ProductoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setStockActual(request.getStockActual() != null ? request.getStockActual() : BigDecimal.ZERO);
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : BigDecimal.ZERO);
        asignarRelaciones(producto, request);

        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto existente = buscarPorId(id);
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            existente.setNombre(request.getNombre());
        }
        if (request.getStockActual() != null) existente.setStockActual(request.getStockActual());
        if (request.getStockMinimo() != null) existente.setStockMinimo(request.getStockMinimo());
        asignarRelaciones(existente, request);
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        productoRepository.deleteById(id);
    }

    private void asignarRelaciones(Producto producto, ProductoRequest request) {
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
            producto.setCategoria(categoria);
        }
        if (request.getUnidadId() != null) {
            UnidadMedida unidad = unidadMedidaRepository.findById(request.getUnidadId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad de medida no encontrada"));
            producto.setUnidad(unidad);
        }
    }
}
