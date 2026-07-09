package com.g2.demo.pruebas.romina.cajanegra;

import com.g2.demo.entity.Producto;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RominaHU08CajaNegraTest {

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
    void cn1_stockIgualAlMinimoApareceEnAlertas() {
        when(productoRepository.findAll()).thenReturn(List.of(producto("Jabon", "10", "10")));

        List<Producto> alertas = service.listarAlertas();

        assertEquals(1, alertas.size());
        assertEquals("Jabon", alertas.get(0).getNombre());
    }

    @Test
    void cn2_stockDebajoDelMinimoApareceEnAlertas() {
        when(productoRepository.findAll()).thenReturn(List.of(producto("Papel higienico", "5", "10")));

        List<Producto> alertas = service.listarAlertas();

        assertEquals(1, alertas.size());
        assertEquals("Papel higienico", alertas.get(0).getNombre());
    }

    @Test
    void cn3_stockEncimaDelMinimoNoApareceEnAlertas() {
        when(productoRepository.findAll()).thenReturn(List.of(producto("Champu", "15", "10")));

        List<Producto> alertas = service.listarAlertas();

        assertTrue(alertas.isEmpty());
    }

    @Test
    void cn4_alertasOrdenadasPorMayorDeficitPrimero() {
        Producto pocoDeficit = producto("Toallas", "9", "10");   // deficit = 1
        Producto muchoDeficit = producto("Sabanas", "2", "10");  // deficit = 8
        when(productoRepository.findAll()).thenReturn(List.of(pocoDeficit, muchoDeficit));

        List<Producto> alertas = service.listarAlertas();

        assertEquals("Sabanas", alertas.get(0).getNombre());
        assertEquals("Toallas", alertas.get(1).getNombre());
    }

    private Producto producto(String nombre, String stockActual, String stockMinimo) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setStockActual(new BigDecimal(stockActual));
        producto.setStockMinimo(new BigDecimal(stockMinimo));
        return producto;
    }
}