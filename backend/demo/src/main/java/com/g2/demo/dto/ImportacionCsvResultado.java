package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//HU-13 - Importacion de productos por CSV
@Getter
@Setter
public class ImportacionCsvResultado {
    private int totalFilas;
    private int exitosos;
    private List<ImportacionCsvError> errores = new ArrayList<>();
}