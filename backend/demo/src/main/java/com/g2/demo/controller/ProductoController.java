package com.g2.demo.controller;

import com.g2.demo.dto.ProductoRequest;
import com.g2.demo.entity.Producto;
import com.g2.demo.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import com.g2.demo.dto.ImportacionCsvResultado;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @GetMapping("/alertas")
    public List<Producto> listarAlertas() {
        return productoService.listarAlertas();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @PostMapping("/importar-csv")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public ImportacionCsvResultado importarCsv(@RequestParam("archivo") MultipartFile archivo) {
        return productoService.importarCsv(archivo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public Producto actualizar(@PathVariable Long id, @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}