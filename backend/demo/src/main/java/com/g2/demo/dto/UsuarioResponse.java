package com.g2.demo.dto;

import com.g2.demo.entity.Usuario;
import lombok.Getter;

@Getter
public class UsuarioResponse {

    private final Long id;
    private final String usuario;
    private final String nombres;
    private final String apellidos;
    private final String email;
    private final String rol;
    private final Boolean activo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.usuario = usuario.getUsername();
        this.nombres = usuario.getNombres();
        this.apellidos = usuario.getApellidos();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
        this.activo = usuario.getActivo();
    }
}
