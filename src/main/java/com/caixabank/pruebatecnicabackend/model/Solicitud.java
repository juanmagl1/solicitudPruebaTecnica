package com.caixabank.pruebatecnicabackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity(name = "solicitud")
@Table
@Data
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "importe_solicitado")
    private double importeSolicitado;
    @Column(name = "divisa")
    private String divisa;
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni", referencedColumnName = "dni")
    private Solicitante solicitante;

}
