package com.g2.demo.pruebas.romina.cajablanca;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.factory.ProductoCsvFactory;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RominaHU13CajaBlancaTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;

    @Test
    void caminoC1_nombreNuloLanzaExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("stockMinimo", "10");
        // "nombre" no esta presente -> fila.get("nombre") devuelve null

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository));
    }

    @Test
    void caminoC2_nombreEnBlancoLanzaExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "   ");
        fila.put("stockMinimo", "10");

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository));
    }

    @Test
    void caminoC3_nombreValidoRetornaProductoRequestCompleto() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Amenidades");
        UnidadMedida unidad = new UnidadMedida();
        unidad.setId(2L);
        unidad.setNombre("Unidad");
        when(categoriaRepository.findByNombreIgnoreCase("Amenidades")).thenReturn(Optional.of(categoria));
        when(unidadMedidaRepository.findByNombreIgnoreCase("Unidad")).thenReturn(Optional.of(unidad));

        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Toallas blancas");
        fila.put("stockActual", "12");
        fila.put("stockMinimo", "50");
        fila.put("categoria", "Amenidades");
        fila.put("unidad", "Unidad");

        ProductoRequest request = ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository);

        assertEquals("Toallas blancas", request.getNombre());
        assertEquals(0, new BigDecimal("12").compareTo(request.getStockActual()));
        assertEquals(0, new BigDecimal("50").compareTo(request.getStockMinimo()));
        assertEquals(1L, request.getCategoriaId());
        assertEquals(2L, request.getUnidadId());
    }

    @Test
    void caminoC4_categoriaInexistenteLanzaExcepcion() {
        when(categoriaRepository.findByNombreIgnoreCase("Inexistente")).thenReturn(Optional.empty());

        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Producto");
        fila.put("stockMinimo", "10");
        fila.put("categoria", "Inexistente");

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository));
    }

    @Test
    void caminoC5_unidadInexistenteLanzaExcepcion() {
        when(unidadMedidaRepository.findByNombreIgnoreCase("Inexistente")).thenReturn(Optional.empty());

        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Producto");
        fila.put("stockMinimo", "10");
        fila.put("unidad", "Inexistente");

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository));
    }

    @Test
    void caminoC6_stockMinimoNoNumericoLanzaExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Producto");
        fila.put("stockMinimo", "abc");

        assertThrows(IllegalArgumentException.class,
                () -> ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository));
    }

    @Test
    void caminoC7_categoriaYUnidadAusentesResuelvenANuloSinExcepcion() {
        Map<String, String> fila = new HashMap<>();
        fila.put("nombre", "Producto sin catalogar");

        ProductoRequest request = ProductoCsvFactory.crearDesdeFila(fila, categoriaRepository, unidadMedidaRepository);

        assertEquals(null, request.getCategoriaId());
        assertEquals(null, request.getUnidadId());
        assertEquals(0, BigDecimal.ZERO.compareTo(request.getStockActual()));
        assertEquals(0, BigDecimal.ZERO.compareTo(request.getStockMinimo()));
    }
}
