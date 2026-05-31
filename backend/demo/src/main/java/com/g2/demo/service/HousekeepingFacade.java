package com.g2.demo.service;

import com.g2.demo.dto.DistribucionRequest;
import com.g2.demo.entity.*;
import com.g2.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class HousekeepingFacade {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private DistribucionRepository distribucionRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Transactional
    public Distribucion registrarConsumoInsumo(DistribucionRequest request) {
        // 1. Validar habitación
        Habitacion habitacion = habitacionRepository.findById(request.getHabitacionId())
                .orElseThrow(() -> new RuntimeException("Error: La habitación no existe."));

        // 2. Validar producto
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Error: El producto no existe."));

        // 3. Validar Stock
        if (producto.getStockActual() < request.getCantidad()) {
            throw new RuntimeException("Error: Stock insuficiente. Solo quedan " + producto.getStockActual() + " unidades.");
        }

        // 4. Descontar stock
        producto.setStockActual(producto.getStockActual() - request.getCantidad());
        productoRepository.save(producto);

        // 5. Registrar la distribución
        Distribucion distribucion = new Distribucion();
        distribucion.setProducto(producto);
        distribucion.setHabitacion(habitacion);
        distribucion.setCantidad(request.getCantidad());
        distribucion.setFechaAsignacion(LocalDateTime.now());
        distribucion.setObservacion("Distribución automática por Housekeeping");
        distribucionRepository.save(distribucion);

        // 6. Historial de movimientos (Salida)
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipo("SALIDA_HOUSEKEEPING");
        movimiento.setCantidad(request.getCantidad());
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        return distribucion;
    }

    public List<Distribucion> listarPorHabitacion(Long habitacionId) {
        return distribucionRepository.findByHabitacionId(habitacionId);
    }

    public List<Distribucion> listarPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return distribucionRepository.findByFechaAsignacionBetween(inicio, fin);
    }
}