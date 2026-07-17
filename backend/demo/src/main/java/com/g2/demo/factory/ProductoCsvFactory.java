package com.g2.demo.factory;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.UnidadMedidaRepository;

import java.math.BigDecimal;
import java.util.Map;

// Patron Factory Method: aca se arma el ProductoRequest a partir de una fila
// del CSV, para no mezclar el parseo con la logica de negocio del service.
public class ProductoCsvFactory {

    public static ProductoRequest crearDesdeFila(Map<String, String> fila,
                                                  CategoriaRepository categoriaRepository,
                                                  UnidadMedidaRepository unidadMedidaRepository) {
        ProductoRequest request = new ProductoRequest();

        String nombre = fila.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        request.setNombre(nombre.trim());
        request.setStockActual(parseDecimal(fila.get("stockActual"), BigDecimal.ZERO));
        request.setStockMinimo(parseDecimal(fila.get("stockMinimo"), BigDecimal.ZERO));
        request.setCategoriaId(resolverCategoriaId(fila.get("categoria"), categoriaRepository));
        request.setUnidadId(resolverUnidadId(fila.get("unidad"), unidadMedidaRepository));
        return request;
    }

    private static Long resolverCategoriaId(String nombreCategoria, CategoriaRepository categoriaRepository) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) return null;
        return categoriaRepository.findByNombreIgnoreCase(nombreCategoria.trim())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + nombreCategoria))
                .getId();
    }

    private static Long resolverUnidadId(String nombreUnidad, UnidadMedidaRepository unidadMedidaRepository) {
        if (nombreUnidad == null || nombreUnidad.isBlank()) return null;
        return unidadMedidaRepository.findByNombreIgnoreCase(nombreUnidad.trim())
                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada: " + nombreUnidad))
                .getId();
    }

    private static BigDecimal parseDecimal(String valor, BigDecimal porDefecto) {
        if (valor == null || valor.isBlank()) return porDefecto;
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numerico invalido: " + valor);
        }
    }
}