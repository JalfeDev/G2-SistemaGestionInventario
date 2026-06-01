package com.g2.demo.dto;

import lombok.Setter;

@Setter
public class LoginRequest {

    private String usuario;
    private String username;
    private String email;

    private String contrasena;
    private String password;
    private String clave;

    public String getUsuario() {
        if (hasText(usuario)) {
            return usuario;
        }
        if (hasText(username)) {
            return username;
        }
        return email;
    }

    public String getContrasena() {
        if (hasText(contrasena)) {
            return contrasena;
        }
        if (hasText(password)) {
            return password;
        }
        return clave;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
