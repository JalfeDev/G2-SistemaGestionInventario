package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String username;
    private String password;
    private String nombres;
    private String apellidos;
    private String nombre;
    private String email;
    private Long rolId;
    private Boolean activo;
}
