package com.caixabank.pruebatecnicabackend.dto.request;

import com.caixabank.pruebatecnicabackend.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateEstadoSolicitudRequest {
    @NotNull(message = "El estado nuevo es obligatorio")
    private EstadoSolicitud nuevoEstado;
}
