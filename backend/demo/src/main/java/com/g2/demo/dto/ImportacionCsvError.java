package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

//HU-13 - Importacion de productos por CSV
@Getter
@Setter
public class ImportacionCsvError {
    private int linea;
    private String nombre;
    private String mensaje;

    public ImportacionCsvError(int linea, String nombre, String mensaje) {
        this.linea = linea;
        this.nombre = nombre;
        this.mensaje = mensaje;
    }
}