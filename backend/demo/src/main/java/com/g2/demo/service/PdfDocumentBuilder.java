package com.g2.demo.service;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

// Motor generico de escritura de PDF (sin librerias externas), compartido por los distintos reportes.
@Component
public class PdfDocumentBuilder {

    private static final int LINEAS_POR_PAGINA = 45;

    public byte[] generar(List<String> lineas) {
        List<List<String>> paginas = paginar(lineas);
        List<String> objetos = new ArrayList<>();
        objetos.add("<< /Type /Catalog /Pages 2 0 R >>");
        objetos.add(construirPaginas(paginas.size()));
        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        for (int index = 0; index < paginas.size(); index++) {
            int contenidoId = 5 + index * 2;
            objetos.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 842] /Resources << /Font << /F1 3 0 R >> >> /Contents " + contenidoId + " 0 R >>");
            byte[] contenido = construirContenido(paginas.get(index)).getBytes(StandardCharsets.ISO_8859_1);
            objetos.add("<< /Length " + contenido.length + " >>\nstream\n"
                    + new String(contenido, StandardCharsets.ISO_8859_1) + "\nendstream");
        }
        return escribirPdf(objetos);
    }

    private List<List<String>> paginar(List<String> lineas) {
        List<List<String>> paginas = new ArrayList<>();
        for (int inicio = 0; inicio < lineas.size(); inicio += LINEAS_POR_PAGINA) {
            paginas.add(lineas.subList(inicio, Math.min(inicio + LINEAS_POR_PAGINA, lineas.size())));
        }
        return paginas.isEmpty() ? List.of(List.of("")) : paginas;
    }

    private String construirPaginas(int cantidad) {
        StringBuilder kids = new StringBuilder();
        for (int index = 0; index < cantidad; index++) {
            kids.append(4 + index * 2).append(" 0 R ");
        }
        return "<< /Type /Pages /Kids [" + kids + "] /Count " + cantidad + " >>";
    }

    private String construirContenido(List<String> lineas) {
        StringBuilder contenido = new StringBuilder("BT /F1 11 Tf 50 795 Td 15 TL\n");
        for (String linea : lineas) {
            contenido.append("(").append(escapar(normalizar(linea))).append(") Tj T*\n");
        }
        return contenido.append("ET").toString();
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private byte[] escribirPdf(List<String> objetos) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        escribir(salida, "%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int index = 0; index < objetos.size(); index++) {
            offsets.add(salida.size());
            escribir(salida, (index + 1) + " 0 obj\n" + objetos.get(index) + "\nendobj\n");
        }
        int inicioXref = salida.size();
        escribir(salida, "xref\n0 " + (objetos.size() + 1) + "\n0000000000 65535 f \n");
        for (int index = 1; index < offsets.size(); index++) {
            escribir(salida, String.format("%010d 00000 n \n", offsets.get(index)));
        }
        escribir(salida, "trailer\n<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\nstartxref\n"
                + inicioXref + "\n%%EOF");
        return salida.toByteArray();
    }

    private void escribir(ByteArrayOutputStream salida, String contenido) {
        salida.writeBytes(contenido.getBytes(StandardCharsets.ISO_8859_1));
    }
}
