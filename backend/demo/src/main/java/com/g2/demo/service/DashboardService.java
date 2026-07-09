package com.g2.demo.service;

import com.g2.demo.dto.DashboardDTO;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.NotificacionStockRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.SolicitudCompraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final SolicitudCompraRepository solicitudCompraRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final NotificacionStockRepository notificacionStockRepository;

    public DashboardService(ProductoRepository productoRepository,
                            SolicitudCompraRepository solicitudCompraRepository,
                            MovimientoInventarioRepository movimientoInventarioRepository,
                            NotificacionStockRepository notificacionStockRepository) {
        this.productoRepository = productoRepository;
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.notificacionStockRepository = notificacionStockRepository;
    }

    public DashboardDTO obtenerDashboard() {
        List<Producto> productos = productoRepository.findAll();
        List<SolicitudCompra> solicitudes = solicitudCompraRepository.findAll();
        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findAll();
        List<NotificacionStock> notificaciones = notificacionStockRepository.findAll();

        List<DashboardDTO.ProductoStockBajo> productosStockBajo = obtenerProductosStockBajo(productos);
        DashboardDTO.Resumen resumen = new DashboardDTO.Resumen(
                productos.size(),
                productosStockBajo.size(),
                solicitudes.stream().filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado())).count(),
                movimientos.size(),
                notificaciones.stream().filter(n -> Boolean.FALSE.equals(n.getResuelta())).count()
        );

        return new DashboardDTO(
                LocalDateTime.now(),
                resumen,
                obtenerStockPorCategoria(productos),
                obtenerSolicitudesPorEstado(solicitudes),
                obtenerMovimientosPorTipo(movimientos),
                productosStockBajo,
                obtenerMovimientosRecientes(movimientos)
        );
    }

    private List<DashboardDTO.StockCategoria> obtenerStockPorCategoria(List<Producto> productos) {
        Map<String, List<Producto>> porCategoria = productos.stream()
                .collect(Collectors.groupingBy(this::nombreCategoria, LinkedHashMap::new, Collectors.toList()));

        return porCategoria.entrySet().stream()
                .map(entry -> new DashboardDTO.StockCategoria(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(Producto::getStockActual)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().size()))
                .sorted(Comparator.comparing(DashboardDTO.StockCategoria::getCategoria))
                .toList();
    }

    private List<DashboardDTO.SolicitudesEstado> obtenerSolicitudesPorEstado(List<SolicitudCompra> solicitudes) {
        return solicitudes.stream()
                .collect(Collectors.groupingBy(this::estadoSolicitud, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new DashboardDTO.SolicitudesEstado(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DashboardDTO.SolicitudesEstado::getEstado))
                .toList();
    }

    private List<DashboardDTO.MovimientosTipo> obtenerMovimientosPorTipo(List<MovimientoInventario> movimientos) {
        Map<String, List<MovimientoInventario>> porTipo = movimientos.stream()
                .collect(Collectors.groupingBy(this::tipoMovimiento, LinkedHashMap::new, Collectors.toList()));

        return porTipo.entrySet().stream()
                .map(entry -> new DashboardDTO.MovimientosTipo(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(MovimientoInventario::getCantidad)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted(Comparator.comparing(DashboardDTO.MovimientosTipo::getTipoMovimiento))
                .toList();
    }

    private List<DashboardDTO.ProductoStockBajo> obtenerProductosStockBajo(List<Producto> productos) {
        return productos.stream()
                .filter(producto -> producto.getStockActual().compareTo(producto.getStockMinimo()) <= 0)
                .sorted(Comparator.comparing((Producto p) -> p.getStockMinimo().subtract(p.getStockActual())).reversed())
                .map(producto -> new DashboardDTO.ProductoStockBajo(
                        producto.getId(),
                        producto.getNombre(),
                        nombreCategoria(producto),
                        producto.getUnidad() != null ? producto.getUnidad().getNombre() : "Sin unidad",
                        producto.getStockActual(),
                        producto.getStockMinimo()))
                .toList();
    }

    private List<DashboardDTO.MovimientoReciente> obtenerMovimientosRecientes(List<MovimientoInventario> movimientos) {
        return movimientos.stream()
                .sorted(Comparator.comparing(MovimientoInventario::getFechaMovimiento).reversed())
                .limit(10)
                .map(movimiento -> new DashboardDTO.MovimientoReciente(
                        movimiento.getId(),
                        tipoMovimiento(movimiento),
                        movimiento.getProducto() != null ? movimiento.getProducto().getNombre() : "Sin producto",
                        movimiento.getCantidad(),
                        movimiento.getStockAnterior(),
                        movimiento.getStockNuevo(),
                        movimiento.getFechaMovimiento()))
                .toList();
    }

    private String nombreCategoria(Producto producto) {
        return producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoria";
    }

    private String estadoSolicitud(SolicitudCompra solicitud) {
        return solicitud.getEstado() != null ? solicitud.getEstado() : "SIN_ESTADO";
    }

    private String tipoMovimiento(MovimientoInventario movimiento) {
        return movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento() : "SIN_TIPO";
    }
}
