package com.g2.demo.pruebas.diago.cajablanca;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Producto;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import com.g2.demo.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceCajaBlancaTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private UnidadMedidaRepository unidadMedidaRepository;
    @Mock private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoExistente;

    @BeforeEach
    void setUp() {
        productoExistente = new Producto();
        productoExistente.setId(1L);
        productoExistente.setNombre("Shampoo");
        productoExistente.setStockActual(new BigDecimal("10.00"));
        productoExistente.setStockMinimo(new BigDecimal("5.00"));
    }

    @Test
    void actualizar_conStockActualDiferenteAlRegistrado_lanzaBadRequest() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));

        ProductoRequest request = new ProductoRequest();
        request.setNombre("Shampoo");
        request.setStockActual(new BigDecimal("50.00")); // intento de edición manual

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productoService.actualizar(1L, request));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).contains("stock actual solo puede modificarse");
    }
}