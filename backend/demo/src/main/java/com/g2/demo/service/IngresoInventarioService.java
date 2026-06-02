package com.g2.demo.service;

import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleIngresoRepository;
import com.g2.demo.repository.IngresoInventarioRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.ProveedorRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IngresoInventarioService extends CrudService<IngresoInventario> {

    private final IngresoInventarioRepository ingresoRepository;
    private final DetalleIngresoRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public IngresoInventarioService(IngresoInventarioRepository repository,
                                    DetalleIngresoRepository detalleRepository,
                                    ProductoRepository productoRepository,
                                    ProveedorRepository proveedorRepository,
                                    UsuarioRepository usuarioRepository,
                                    MovimientoInventarioRepository movimientoRepository) {
        super(repository, "Ingreso de inventario");
        this.ingresoRepository = repository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<DetalleIngreso> listarHistorial() {
        return detalleRepository.findAllByOrderByIngresoInventarioFechaIngresoDesc();
    }

    @Transactional
    public DetalleIngreso registrar(RegistrarEntradaRequest request, String username) {
        validar(request);
        Producto producto = productoRepository.findWithLockById(request.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        Usuario usuario = buscarUsuario(username);

        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockNuevo = stockAnterior.add(request.getCantidad());
        BigDecimal costoTotal = request.getCantidad().multiply(request.getCostoUnitario());

        IngresoInventario ingreso = new IngresoInventario();
        ingreso.setFechaIngreso(LocalDateTime.now());
        ingreso.setObservacion(request.getObservacion());
        ingreso.setUsuario(usuario);
        ingresoRepository.save(ingreso);

        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setIngresoInventario(ingreso);
        detalle.setProducto(producto);
        detalle.setProveedor(proveedor);
        detalle.setCantidad(request.getCantidad());
        detalle.setCostoUnitario(request.getCostoUnitario());
        detalle.setCostoTotal(costoTotal);
        detalleRepository.save(detalle);

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);
        movimientoRepository.save(crearMovimiento("ENTRADA", request.getCantidad(), stockAnterior, stockNuevo, producto, usuario));
        return detalle;
    }

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    private MovimientoInventario crearMovimiento(String tipo, BigDecimal cantidad, BigDecimal anterior,
                                                  BigDecimal nuevo, Producto producto, Usuario usuario) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(anterior);
        movimiento.setStockNuevo(nuevo);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        return movimiento;
    }

    private void validar(RegistrarEntradaRequest request) {
        if (request.getProductoId() == null || request.getProveedorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto y proveedor son obligatorios");
        }
        if (request.getCantidad() == null || request.getCantidad().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
        }
        if (request.getCostoUnitario() == null || request.getCostoUnitario().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio unitario no puede ser negativo");
        }
    }
}
