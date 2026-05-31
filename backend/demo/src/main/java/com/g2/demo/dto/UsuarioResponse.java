package com.g2.demo.dto;

import com.g2.demo.entity.Usuario;
import lombok.Getter;

@Getter
public class UsuarioResponse {

    private final Long id;
    private final String usuario;
    private final String nombre;
    private final String email;
    private final String rol;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.usuario = usuario.getUsername();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
    }
}
