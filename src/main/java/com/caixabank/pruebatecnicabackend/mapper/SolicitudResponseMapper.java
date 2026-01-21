package com.caixabank.pruebatecnicabackend.mapper;

import com.caixabank.pruebatecnicabackend.dto.response.SolicitudResponse;
import com.caixabank.pruebatecnicabackend.model.Solicitud;

import java.math.BigDecimal;

public class SolicitudResponseMapper {

    public static SolicitudResponse toResponse(Solicitud solicitud) {
        if (solicitud == null) return null;

        SolicitudResponse response = new SolicitudResponse();
        response.setNombreSolicitante(solicitud.getSolicitante().getNombre());
        response.setImporteSolicitado(BigDecimal.valueOf(solicitud.getImporteSolicitado()));
        response.setDivisa(solicitud.getDivisa());
        response.setDni(solicitud.getSolicitante().getDni());
        response.setFechaCreacion(solicitud.getFechaCreacion());
        response.setEstadoSolicitud(solicitud.getEstadoSolicitud());
        return response;
    }
}
