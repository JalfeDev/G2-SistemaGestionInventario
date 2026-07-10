package com.g2.demo.factory;

import com.g2.demo.dto.ProductoRequest;

import java.math.BigDecimal;
import java.util.Map;

// Patron Factory Method: aca se arma el ProductoRequest a partir de una fila
// del CSV, para no mezclar el parseo con la logica de negocio del service.
public class ProductoCsvFactory {

    public static ProductoRequest crearDesdeFila(Map<String, String> fila) {
        ProductoRequest request = new ProductoRequest();

        String nombre = fila.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        request.setNombre(nombre.trim());
        request.setStockActual(parseDecimal(fila.get("stockActual"), BigDecimal.ZERO));
        request.setStockMinimo(parseDecimal(fila.get("stockMinimo"), BigDecimal.ZERO));
        request.setCategoriaId(parseLong(fila.get("categoriaId")));
        request.setUnidadId(parseLong(fila.get("unidadId")));
        return request;
    }

    private static BigDecimal parseDecimal(String valor, BigDecimal porDefecto) {
        if (valor == null || valor.isBlank()) return porDefecto;
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numerico invalido: " + valor);
        }
    }

    private static Long parseLong(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id invalido: " + valor);
        }
    }
}