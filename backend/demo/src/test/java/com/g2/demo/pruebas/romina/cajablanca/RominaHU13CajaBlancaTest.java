package com.g2.demo.pruebas.romina.cajablanca;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.factory.ProductoCsvFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RominaHU13CajaBlancaTest {

    @Test
    void caminoC1_nombreNuloLanzaExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("stockMinimo", "10");
        // "nombre" no esta presente -> fila.get("nombre") devuelve null

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila));
    }

    @Test
    void caminoC2_nombreEnBlancoLanzaExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "   ");
        fila.put("stockMinimo", "10");

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila));
    }

    @Test
    void caminoC3_nombreValidoRetornaProductoRequestCompleto() {
        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Toallas blancas");
        fila.put("stockActual", "12");
        fila.put("stockMinimo", "50");
        fila.put("categoriaId", "1");
        fila.put("unidadId", "2");

        ProductoRequest request = ProductoCsvFactory.crearDesdeFila(fila);

        assertEquals("Toallas blancas", request.getNombre());
        assertEquals(0, new BigDecimal("12").compareTo(request.getStockActual()));
        assertEquals(0, new BigDecimal("50").compareTo(request.getStockMinimo()));
        assertEquals(1L, request.getCategoriaId());
        assertEquals(2L, request.getUnidadId());
    }
}