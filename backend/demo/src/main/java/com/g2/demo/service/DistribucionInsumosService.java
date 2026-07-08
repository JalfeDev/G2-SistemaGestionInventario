package com.g2.demo.service;

import com.g2.demo.dto.RegistrarDistribucionRequest;
import com.g2.demo.entity.DetalleDistribucion;
import com.g2.demo.entity.DistribucionInsumos;
import com.g2.demo.entity.Habitacion;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleDistribucionRepository;
import com.g2.demo.repository.DistribucionInsumosRepository;
import com.g2.demo.repository.HabitacionRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DistribucionInsumosService extends CrudService<DistribucionInsumos> {

    private final DistribucionInsumosRepository distribucionRepository;
    private final DetalleDistribucionRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final NotificacionStockService notificacionStockService;

    public DistribucionInsumosService(DistribucionInsumosRepository repository,
                                      DetalleDistribucionRepository detalleRepository,
                                      ProductoRepository productoRepository,
                                      HabitacionRepository habitacionRepository,
                                      UsuarioRepository usuarioRepository,
                                      MovimientoInventarioRepository movimientoRepository,
                                      NotificacionStockService notificacionStockService) {
        super(repository, "Distribucion de insumos");
        this.distribucionRepository = repository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.habitacionRepository = habitacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.notificacionStockService = notificacionStockService;
    }

    public List<DetalleDistribucion> listarHistorial(LocalDate fechaInicio, LocalDate fechaFin, Long habitacionId) {
        return detalleRepository.findAllByOrderByDistribucionFechaDesc().stream()
                .filter(detalle -> habitacionId == null || detalle.getDistribucion().getHabitacion().getId().equals(habitacionId))
                .filter(detalle -> fechaInicio == null || !detalle.getDistribucion().getFecha().toLocalDate().isBefore(fechaInicio))
                .filter(detalle -> fechaFin == null || !detalle.getDistribucion().getFecha().toLocalDate().isAfter(fechaFin))
                .toList();
    }

    @Transactional
    public DetalleDistribucion registrar(RegistrarDistribucionRequest request, String username) {
        validar(request);
        Producto producto = productoRepository.findWithLockById(request.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        if (request.getCantidad().compareTo(producto.getStockActual()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para registrar la distribucion");
        }
        Habitacion habitacion = habitacionRepository.findById(request.getHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitacion no encontrada"));
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockNuevo = stockAnterior.subtract(request.getCantidad());

        DistribucionInsumos distribucion = new DistribucionInsumos();
        distribucion.setFecha(LocalDateTime.now());
        distribucion.setHabitacion(habitacion);
        distribucion.setUsuario(usuario);
        distribucionRepository.save(distribucion);

        DetalleDistribucion detalle = new DetalleDistribucion();
        detalle.setDistribucion(distribucion);
        detalle.setProducto(producto);
        detalle.setCantidad(request.getCantidad());
        detalleRepository.save(detalle);

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(request.getCantidad());
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimientoRepository.save(movimiento);
        notificacionStockService.evaluarStockCritico(producto);
        return detalle;
    }

    private void validar(RegistrarDistribucionRequest request) {
        if (request.getProductoId() == null || request.getHabitacionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto y habitacion son obligatorios");
        }
        if (request.getCantidad() == null || request.getCantidad().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
        }
    }
}
