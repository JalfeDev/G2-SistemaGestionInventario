package com.g2.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solicitudes_compra")
public class SolicitudCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_solicitante", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aprobador")
    private Usuario aprobador;
}
