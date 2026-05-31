package com.g2.demo.service;

import com.g2.demo.dto.EntradaInsumoRequest;
import com.g2.demo.entity.EntradaInsumo;
import com.g2.demo.entity.MovimientoInventario;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.Proveedor;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.EntradaInsumoRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;

@Service
public class AlmacenFacade {

    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final EntradaInsumoRepository entradaInsumoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    // Inyección por Constructor (Cumpliendo el principio de inversión de dependencias de SOLID)
    public AlmacenFacade(ProductoService productoService,
                         ProveedorService proveedorService,
                         UsuarioRepository usuarioRepository,
                         ProductoRepository productoRepository,
                         EntradaInsumoRepository entradaInsumoRepository,
                         MovimientoInventarioRepository movimientoInventarioRepository) {
        this.productoService = productoService;
        this.proveedorService = proveedorService;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.entradaInsumoRepository = entradaInsumoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Transactional // Si falla cualquier inserción en MySQL, hace Rollback automático de todo
    public void registrarEntradaInsumo(EntradaInsumoRequest request, String username) {

        // 1. Criterios de Aceptación: Validaciones de negocio robustas
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero.");
        }
        if (request.getPrecioUnitario() == null || request.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio por unidad no puede ser negativo.");
        }

        // 2. Uso unificado de subsistemas a través de la Fachada
        Producto producto = productoService.buscarPorId(request.getProductoId());
        Proveedor proveedor = proveedorService.buscarPorId(request.getProveedorId());

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario de sesión no válido"));

        // 3. Criterio de Aceptación: El stock aumenta automáticamente
        producto.setStockActual(producto.getStockActual() + request.getCantidad());
        productoRepository.save(producto);

        // 4. Criterio de Aceptación: Cálculo automático del costo total
        BigDecimal costoTotal = request.getPrecioUnitario().multiply(BigDecimal.valueOf(request.getCantidad()));

        // 5. Persistencia del registro histórico de la entrada
        EntradaInsumo entrada = new EntradaInsumo();
        entrada.setProducto(producto);
        entrada.setProveedor(proveedor);
        entrada.setCantidad(request.getCantidad());
        entrada.setPrecioUnitario(request.getPrecioUnitario());
        entrada.setCostoTotal(costoTotal);
        entrada.setUsuario(usuario);
        entradaInsumoRepository.save(entrada);

        // 6. Integración del ecosistema: Guardar trazabilidad en MovimientoInventario
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo("ENTRADA");
        movimiento.setCantidad(request.getCantidad());
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setObservacion("Entrada registrada del proveedor: " + proveedor.getNombre());
        movimientoInventarioRepository.save(movimiento);
    }
}