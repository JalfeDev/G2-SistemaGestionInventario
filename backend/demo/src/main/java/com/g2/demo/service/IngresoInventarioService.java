package com.g2.demo.service;

import com.g2.demo.dto.HistorialPrecioItem;
import com.g2.demo.dto.HistorialPreciosResponse;
import com.g2.demo.dto.RegistrarEntradaRequest;
import com.g2.demo.entity.DetalleSolicitud;
import com.g2.demo.entity.DetalleIngreso;
import com.g2.demo.entity.IngresoInventario;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.SolicitudCompra;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.DetalleIngresoRepository;
import com.g2.demo.repository.DetalleSolicitudRepository;
import com.g2.demo.repository.IngresoInventarioRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.ProveedorRepository;
import com.g2.demo.repository.SolicitudCompraRepository;
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

    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";
    private static final String ESTADO_RECHAZADO = "RECHAZADO";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private final IngresoInventarioRepository ingresoRepository;
    private final DetalleIngresoRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final NotificacionStockService notificacionStockService;
    private final SolicitudCompraRepository solicitudCompraRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;

    public IngresoInventarioService(IngresoInventarioRepository repository,
                                    DetalleIngresoRepository detalleRepository,
                                    ProductoRepository productoRepository,
                                    ProveedorRepository proveedorRepository,
                                    UsuarioRepository usuarioRepository,
                                    MovimientoInventarioRepository movimientoRepository,
                                    NotificacionStockService notificacionStockService,
                                    SolicitudCompraRepository solicitudCompraRepository,
                                    DetalleSolicitudRepository detalleSolicitudRepository) {
        super(repository, "Ingreso de inventario");
        this.ingresoRepository = repository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.notificacionStockService = notificacionStockService;
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
    }

    public List<DetalleIngreso> listarHistorial() {
        return detalleRepository.findAllByOrderByIngresoInventarioFechaIngresoDesc();
    }

    @Transactional
    public DetalleIngreso registrar(RegistrarEntradaRequest request, String username) {
        validar(request);
        DetalleSolicitud detalleSolicitud = validarSolicitudSiAplica(request);
        Producto producto = productoRepository.findWithLockById(request.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        Usuario usuario = buscarUsuario(username);
        validarEntradaManual(request, usuario);
        SolicitudCompra solicitud = detalleSolicitud != null ? detalleSolicitud.getSolicitud() : null;

        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockNuevo = stockAnterior.add(request.getCantidad());
        BigDecimal costoTotal = request.getCantidad().multiply(request.getCostoUnitario());

        IngresoInventario ingreso = new IngresoInventario();
        ingreso.setFechaIngreso(LocalDateTime.now());
        ingreso.setObservacion(request.getObservacion());
        ingreso.setUsuario(usuario);
        ingreso.setSolicitud(solicitud);
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
        if (solicitud != null) {
            solicitud.setEstado(ESTADO_ATENDIDA);
            solicitudCompraRepository.save(solicitud);
        }
        notificacionStockService.evaluarStockCritico(producto);
        return detalle;
    }

    private DetalleSolicitud validarSolicitudSiAplica(RegistrarEntradaRequest request) {
        if (request.getSolicitudId() == null) {
            return null;
        }

        SolicitudCompra solicitud = solicitudCompraRepository.findWithLockById(request.getSolicitudId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        validarEstadoSolicitud(solicitud);

        List<DetalleSolicitud> detalles = detalleSolicitudRepository.findBySolicitudId(solicitud.getId());
        if (detalles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud no tiene productos para recibir");
        }
        if (detalles.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La recepcion desde solicitud solo admite solicitudes de un producto");
        }

        DetalleSolicitud detalle = detalles.get(0);
        if (!detalle.getProducto().getId().equals(request.getProductoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El producto de la entrada debe coincidir con el producto solicitado");
        }
        if (request.getCantidad().compareTo(detalle.getCantidadSolicitada()) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad recibida debe coincidir con la cantidad aprobada");
        }
        detalle.setSolicitud(solicitud);
        return detalle;
    }

    private void validarEntradaManual(RegistrarEntradaRequest request, Usuario usuario) {
        if (request.getSolicitudId() != null || usuario.getRol() == null) {
            return;
        }
        if ("ALMACEN".equals(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El encargado de almacen debe registrar entradas desde una solicitud aprobada");
        }
    }

    private void validarEstadoSolicitud(SolicitudCompra solicitud) {
        if (ESTADO_APROBADO.equals(solicitud.getEstado())) {
            return;
        }
        if (ESTADO_PENDIENTE.equals(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La solicitud pendiente no puede generar una entrada");
        }
        if (ESTADO_RECHAZADO.equals(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La solicitud rechazada no puede generar una entrada");
        }
        if (ESTADO_ATENDIDA.equals(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La solicitud ya fue atendida");
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La solicitud debe estar aprobada para generar una entrada");
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

    //HU - Historial de precios por proveedor
    public HistorialPreciosResponse consultarHistorialPrecios(Long productoId, Long proveedorId) {
        // 1. Obtener del repositorio del más reciente al más antiguo
        List<DetalleIngreso> historialFiltrado;
        if (productoId != null && proveedorId != null) {
            historialFiltrado = detalleRepository
                    .findByProducto_IdAndProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(productoId, proveedorId);
        } else if (productoId != null) {
            historialFiltrado = detalleRepository.findByProducto_IdOrderByIngresoInventarioFechaIngresoDesc(productoId);
        } else if (proveedorId != null) {
            historialFiltrado = detalleRepository.findByProveedor_IdOrderByIngresoInventarioFechaIngresoDesc(proveedorId);
        } else {
            historialFiltrado = detalleRepository.findAllByOrderByIngresoInventarioFechaIngresoDesc();
        }

        // 2. Calcular el precio promedio de los elementos filtrados
        BigDecimal precioPromedio = BigDecimal.ZERO;
        if (!historialFiltrado.isEmpty()) {
            BigDecimal sumaPrecios = historialFiltrado.stream()
                    .map(DetalleIngreso::getCostoUnitario)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            precioPromedio = sumaPrecios.divide(
                    BigDecimal.valueOf(historialFiltrado.size()),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        // 3. Empaquetar y retornar el DTO de respuesta con items planos
        List<HistorialPrecioItem> items = historialFiltrado.stream()
                .map(HistorialPrecioItem::new)
                .toList();

        HistorialPreciosResponse response = new HistorialPreciosResponse();
        response.setHistorial(items);
        response.setPrecioPromedio(precioPromedio);
        return response;
    }
}
