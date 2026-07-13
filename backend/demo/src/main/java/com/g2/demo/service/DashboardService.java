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

    private static final int DIAS_VENTANA_CONSUMO = 30;
    private static final String TIPO_ENTRADA = "ENTRADA";
    private static final String TIPO_SALIDA = "SALIDA";

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

        LocalDateTime finVentana = LocalDateTime.now();
        LocalDateTime inicioVentana = finVentana.minusDays(DIAS_VENTANA_CONSUMO);
        List<MovimientoInventario> salidas30Dias = movimientoInventarioRepository
                .findByTipoMovimientoAndFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThanOrderByFechaMovimientoAsc(
                        TIPO_SALIDA, inicioVentana, finVentana);
        List<MovimientoInventario> movimientos30Dias = movimientoInventarioRepository
                .findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(inicioVentana, finVentana);

        return new DashboardDTO(
                LocalDateTime.now(),
                resumen,
                obtenerStockPorCategoria(productos),
                obtenerSolicitudesPorEstado(solicitudes),
                obtenerMovimientosPorTipo(movimientos),
                productosStockBajo,
                obtenerMovimientosRecientes(movimientos),
                obtenerTopProductosConsumidos(salidas30Dias),
                obtenerConsumoPorCategoria(salidas30Dias),
                obtenerVariacionStock(productos, movimientos30Dias)
        );
    }

    private List<DashboardDTO.ProductoConsumoDTO> obtenerTopProductosConsumidos(List<MovimientoInventario> salidas) {
        Map<Long, ProductoConsumoAcumulado> acumulado = new LinkedHashMap<>();
        for (MovimientoInventario movimiento : salidas) {
            Producto producto = movimiento.getProducto();
            acumulado.computeIfAbsent(producto.getId(), id -> new ProductoConsumoAcumulado(producto))
                    .sumar(movimiento.getCantidad());
        }
        return acumulado.values().stream()
                .sorted(Comparator.comparing((ProductoConsumoAcumulado a) -> a.cantidad).reversed())
                .limit(5)
                .map(a -> new DashboardDTO.ProductoConsumoDTO(
                        a.producto.getId(),
                        a.producto.getNombre(),
                        a.cantidad,
                        a.producto.getUnidad() != null ? a.producto.getUnidad().getNombre() : "Sin unidad"))
                .toList();
    }

    private List<DashboardDTO.CategoriaConsumoDTO> obtenerConsumoPorCategoria(List<MovimientoInventario> salidas) {
        Map<String, BigDecimal> acumulado = new LinkedHashMap<>();
        for (MovimientoInventario movimiento : salidas) {
            String categoria = nombreCategoria(movimiento.getProducto());
            acumulado.merge(categoria, movimiento.getCantidad(), BigDecimal::add);
        }
        return acumulado.entrySet().stream()
                .map(entry -> new DashboardDTO.CategoriaConsumoDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DashboardDTO.CategoriaConsumoDTO::getCantidadConsumida).reversed())
                .toList();
    }

    private List<DashboardDTO.VariacionStockDTO> obtenerVariacionStock(List<Producto> productos,
                                                                        List<MovimientoInventario> movimientos30Dias) {
        Map<Long, BigDecimal> entradas = new LinkedHashMap<>();
        Map<Long, BigDecimal> salidas = new LinkedHashMap<>();
        for (MovimientoInventario movimiento : movimientos30Dias) {
            Long idProducto = movimiento.getProducto().getId();
            if (TIPO_ENTRADA.equals(movimiento.getTipoMovimiento())) {
                entradas.merge(idProducto, movimiento.getCantidad(), BigDecimal::add);
            } else if (TIPO_SALIDA.equals(movimiento.getTipoMovimiento())) {
                salidas.merge(idProducto, movimiento.getCantidad(), BigDecimal::add);
            }
        }
        return productos.stream()
                .map(producto -> {
                    BigDecimal entradasProducto = entradas.getOrDefault(producto.getId(), BigDecimal.ZERO);
                    BigDecimal salidasProducto = salidas.getOrDefault(producto.getId(), BigDecimal.ZERO);
                    BigDecimal stockHaceTreintaDias = producto.getStockActual().subtract(entradasProducto).add(salidasProducto);
                    BigDecimal variacionStock = producto.getStockActual().subtract(stockHaceTreintaDias);
                    return new DashboardDTO.VariacionStockDTO(
                            producto.getId(), producto.getNombre(), producto.getStockActual(),
                            stockHaceTreintaDias, variacionStock);
                })
                .toList();
    }

    private static class ProductoConsumoAcumulado {
        private final Producto producto;
        private BigDecimal cantidad = BigDecimal.ZERO;

        private ProductoConsumoAcumulado(Producto producto) {
            this.producto = producto;
        }

        private void sumar(BigDecimal valor) {
            this.cantidad = this.cantidad.add(valor);
        }
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
