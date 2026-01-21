package com.caixabank.pruebatecnicabackend.dto.response;

import com.caixabank.pruebatecnicabackend.model.EstadoSolicitud;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SolicitudResponse {

    private String nombreSolicitante;
    private BigDecimal importeSolicitado;
    private String divisa;
    private String dni;
    private EstadoSolicitud estadoSolicitud;
    private LocalDateTime fechaCreacion;
}
