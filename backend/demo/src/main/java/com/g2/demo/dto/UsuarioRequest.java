package com.g2.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String username;
    private String password;
    private String nombre;
    private String email;
    private Long rolId;
}