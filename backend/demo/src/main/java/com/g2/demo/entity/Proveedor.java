package com.g2.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(unique = true)
    private Integer ruc;

    @Column(length = 30)
    private String telefono;

    @Column(name = "correo", length = 120)
    private String correo;

    @Column(length = 250)
    private String direccion;

    public String getEmail() {
        return correo;
    }

    public void setEmail(String email) {
        this.correo = email;
    }
}
