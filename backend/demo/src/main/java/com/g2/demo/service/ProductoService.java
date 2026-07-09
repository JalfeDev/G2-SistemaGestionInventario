package com.g2.demo.service;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Categoria;
import com.g2.demo.entity.Producto;
import com.g2.demo.entity.UnidadMedida;
import com.g2.demo.repository.CategoriaRepository;
import com.g2.demo.repository.MovimientoInventarioRepository;
import com.g2.demo.repository.ProductoRepository;
import com.g2.demo.repository.UnidadMedidaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.g2.demo.dto.ImportacionCsvError;
import com.g2.demo.dto.ImportacionCsvResultado;
import com.g2.demo.factory.ProductoCsvFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final NotificacionStockService notificacionStockService;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           UnidadMedidaRepository unidadMedidaRepository,
                           MovimientoInventarioRepository movimientoInventarioRepository,
                           NotificacionStockService notificacionStockService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.notificacionStockService = notificacionStockService;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public List<Producto> listarAlertas() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual().compareTo(p.getStockMinimo()) <= 0)
                .sorted(Comparator.comparing((Producto p) -> p.getStockMinimo().subtract(p.getStockActual())).reversed())
                .toList();
    }

    public Producto crear(ProductoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        validarStock(request);
        validarRelacionesRequeridas(request);
        validarDuplicado(request.getNombre(), request.getCategoriaId(), null);
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setStockActual(request.getStockActual() != null ? request.getStockActual() : BigDecimal.ZERO);
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : BigDecimal.ZERO);
        asignarRelaciones(producto, request);

        Producto guardado = productoRepository.save(producto);
        notificacionStockService.evaluarStockCritico(guardado);
        return guardado;
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto existente = buscarPorId(id);
        validarStock(request);
        if (request.getStockActual() != null && request.getStockActual().compareTo(existente.getStockActual()) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El stock actual solo puede modificarse mediante entradas o distribuciones");
        }
        Long categoriaId = request.getCategoriaId() != null
                ? request.getCategoriaId()
                : existente.getCategoria() != null ? existente.getCategoria().getId() : null;
        String nombre = request.getNombre() != null && !request.getNombre().isBlank()
                ? request.getNombre()
                : existente.getNombre();
        validarDuplicado(nombre, categoriaId, id);
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            existente.setNombre(request.getNombre());
        }
        if (request.getStockMinimo() != null) existente.setStockMinimo(request.getStockMinimo());
        asignarRelaciones(existente, request);
        Producto guardado = productoRepository.save(existente);
        notificacionStockService.evaluarStockCritico(guardado);
        return guardado;
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        if (movimientoInventarioRepository.existsByProductoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un producto con movimientos registrados");
        }
        productoRepository.deleteById(id);
    }

    private void validarRelacionesRequeridas(ProductoRequest request) {
        if (request.getCategoriaId() == null || request.getUnidadId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria y unidad de medida son obligatorias");
        }
    }

    private void validarStock(ProductoRequest request) {
        if (request.getStockActual() != null && request.getStockActual().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock actual no puede ser negativo");
        }
        if (request.getStockMinimo() != null && request.getStockMinimo().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock minimo no puede ser negativo");
        }
    }

    private void validarDuplicado(String nombre, Long categoriaId, Long idActual) {
        if (categoriaId == null) {
            return;
        }
        boolean existe = idActual == null
                ? productoRepository.existsByNombreIgnoreCaseAndCategoriaId(nombre, categoriaId)
                : productoRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(nombre, categoriaId, idActual);
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese nombre en la categoria seleccionada");
        }
    }

    private void asignarRelaciones(Producto producto, ProductoRequest request) {
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
            producto.setCategoria(categoria);
        }
        if (request.getUnidadId() != null) {
            UnidadMedida unidad = unidadMedidaRepository.findById(request.getUnidadId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad de medida no encontrada"));
            producto.setUnidad(unidad);
        }
    }
    public ImportacionCsvResultado importarCsv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe adjuntar un archivo CSV");
        }

        ImportacionCsvResultado resultado = new ImportacionCsvResultado();

        try (BufferedReader lector = new BufferedReader(new InputStreamReader(archivo.getInputStream()))) {
            String encabezado = lector.readLine();
            if (encabezado == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo esta vacio");
            }
            String[] columnas = encabezado.split(",");
            for (String requerida : List.of("nombre", "stockMinimo")) {
                boolean existe = false;
                for (String columna : columnas) {
                    if (columna.trim().equalsIgnoreCase(requerida)) existe = true;
                }
                if (!existe) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta la columna obligatoria: " + requerida);
                }
            }

            String linea;
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) continue;
                resultado.setTotalFilas(resultado.getTotalFilas() + 1);

                String[] valores = linea.split(",", -1);
                Map<String, String> fila = new HashMap<>();
                for (int i = 0; i < columnas.length && i < valores.length; i++) {
                    fila.put(columnas[i].trim(), valores[i].trim());
                }

                try {
                    crear(ProductoCsvFactory.crearDesdeFila(fila));
                    resultado.setExitosos(resultado.getExitosos() + 1);
                } catch (IllegalArgumentException | ResponseStatusException e) {
                    String mensaje = e instanceof ResponseStatusException rse ? rse.getReason() : e.getMessage();
                    resultado.getErrores().add(new ImportacionCsvError(numeroLinea, fila.get("nombre"), mensaje));
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo CSV");
        }

        return resultado;
    }
}
