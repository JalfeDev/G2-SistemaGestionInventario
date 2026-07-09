package com.g2.demo.pruebas.romina.unitarias;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import com.g2.demo.service.NotificacionStockService;
import com.g2.demo.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RominaHU05UnitariasTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;
    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;
    @Mock
    private NotificacionStockService notificacionStockService;

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(productoRepository, categoriaRepository,
                unidadMedidaRepository, movimientoInventarioRepository, notificacionStockService);
    }

    @Test
    void crearProductoValidoLoGuardaYEvaluaStockCritico() {
        ProductoRequest request = request("Shampoo", "20", "5", 1L, 2L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria(1L, "Amenidades")));
        when(unidadMedidaRepository.findById(2L)).thenReturn(Optional.of(unidad(2L, "Unidad")));
        when(productoRepository.existsByNombreIgnoreCaseAndCategoriaId("Shampoo", 1L)).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto guardado = service.crear(request);

        assertEquals("Shampoo", guardado.getNombre());
        verify(productoRepository).save(any(Producto.class));
        verify(notificacionStockService).evaluarStockCritico(guardado);
    }

    @Test
    void crearProductoConNombreVacioLanzaExcepcion() {
        ProductoRequest request = request("", "20", "5", 1L, 2L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.crear(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearProductoSinCategoriaNiUnidadLanzaExcepcion() {
        ProductoRequest request = request("Toallas", "20", "5", null, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.crear(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Categoria y unidad de medida son obligatorias", exception.getReason());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearProductoDuplicadoEnLaMismaCategoriaLanzaExcepcion() {
        ProductoRequest request = request("Jabon", "20", "5", 1L, 2L);
        when(productoRepository.existsByNombreIgnoreCaseAndCategoriaId("Jabon", 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.crear(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(productoRepository, never()).save(any());
    }

    private ProductoRequest request(String nombre, String stockActual, String stockMinimo, Long categoriaId, Long unidadId) {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(nombre);
        request.setStockActual(new BigDecimal(stockActual));
        request.setStockMinimo(new BigDecimal(stockMinimo));
        request.setCategoriaId(categoriaId);
        request.setUnidadId(unidadId);
        return request;
    }

    private Categoria categoria(Long id, String nombre) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        return categoria;
    }

    private UnidadMedida unidad(Long id, String nombre) {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setId(id);
        unidad.setNombre(nombre);
        return unidad;
    }
}