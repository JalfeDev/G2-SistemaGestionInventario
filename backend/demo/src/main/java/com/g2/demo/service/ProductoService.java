package com.g2.demo.service;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           UnidadMedidaRepository unidadMedidaRepository,
                           MovimientoInventarioRepository movimientoInventarioRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
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
                .sorted(Comparator.comparing((Producto p) -> p.getStockMinimo().subtract(p.getStockActual())).reversed())
                .toList();
    }

    public Producto crear(ProductoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        validarStock(request);
        validarRelacionesRequeridas(request);
        validarDuplicado(request.getNombre(), request.getCategoriaId(), null);
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setStockActual(request.getStockActual() != null ? request.getStockActual() : BigDecimal.ZERO);
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : BigDecimal.ZERO);
        asignarRelaciones(producto, request);

        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto existente = buscarPorId(id);
        validarStock(request);
        if (request.getStockActual() != null && request.getStockActual().compareTo(existente.getStockActual()) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El stock actual solo puede modificarse mediante entradas o distribuciones");
        }
        Long categoriaId = request.getCategoriaId() != null
                ? request.getCategoriaId()
                : existente.getCategoria() != null ? existente.getCategoria().getId() : null;
        String nombre = request.getNombre() != null && !request.getNombre().isBlank()
                ? request.getNombre()
                : existente.getNombre();
        validarDuplicado(nombre, categoriaId, id);
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            existente.setNombre(request.getNombre());
        }
        if (request.getStockMinimo() != null) existente.setStockMinimo(request.getStockMinimo());
        asignarRelaciones(existente, request);
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        if (movimientoInventarioRepository.existsByProductoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un producto con movimientos registrados");
        }
        productoRepository.deleteById(id);
    }

    private void validarRelacionesRequeridas(ProductoRequest request) {
        if (request.getCategoriaId() == null || request.getUnidadId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria y unidad de medida son obligatorias");
        }
    }

    private void validarStock(ProductoRequest request) {
        if (request.getStockActual() != null && request.getStockActual().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock actual no puede ser negativo");
        }
        if (request.getStockMinimo() != null && request.getStockMinimo().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock minimo no puede ser negativo");
        }
    }

    private void validarDuplicado(String nombre, Long categoriaId, Long idActual) {
        if (categoriaId == null) {
            return;
        }
        boolean existe = idActual == null
                ? productoRepository.existsByNombreIgnoreCaseAndCategoriaId(nombre, categoriaId)
                : productoRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(nombre, categoriaId, idActual);
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese nombre en la categoria seleccionada");
        }
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
