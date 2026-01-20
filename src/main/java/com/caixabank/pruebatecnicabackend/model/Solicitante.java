package com.caixabank.pruebatecnicabackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "solicitante")
@Table
@Data
public class Solicitante {
    @Id
    @Column(name = "dni")
    private String dni;
    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "solicitante")
    private List<Solicitud> solicitudes;
}
