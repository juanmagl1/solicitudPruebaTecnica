package com.caixabank.pruebatecnicabackend.service;

import com.caixabank.pruebatecnicabackend.dto.request.SolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.request.UpdateEstadoSolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.response.SolicitudResponse;
import com.caixabank.pruebatecnicabackend.exception.ApiException;
import com.caixabank.pruebatecnicabackend.mapper.SolicitudResponseMapper;
import com.caixabank.pruebatecnicabackend.model.EstadoSolicitud;
import com.caixabank.pruebatecnicabackend.model.Solicitante;
import com.caixabank.pruebatecnicabackend.model.Solicitud;
import com.caixabank.pruebatecnicabackend.repository.SolicitudRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final SolicitanteService solicitanteService;

    /**
     * Crea una solicitud.
     *
     * @param solicitudRequest la solicitud a crear.
     * @return la respuesta de la solicitud creada.
     */
    public SolicitudResponse crearSolicitud(SolicitudRequest solicitudRequest) {
        Solicitud solicitud = solicitudRepository.findBySolicitanteDniAndDivisaAndImporteSolicitado(
                solicitudRequest.getDni(),
                solicitudRequest.getDivisa(),
                solicitudRequest.getImporteSolicitado().doubleValue()
        );
        if (solicitud != null && solicitud.getEstadoSolicitud() == EstadoSolicitud.PENDIENTE) {
            throw new ApiException(
                    "SOLICITUD_PENDIENTE",
                    "Ya existe una solicitud para ese solicitante y divisa y está en proceso de validación.",
                    HttpStatus.BAD_REQUEST
            );
        }
        Solicitud nuevaSolicitud = mapToEntity(solicitudRequest);
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(nuevaSolicitud));
    }

    /**
     * Devuelve una lista con todas las solicitudes.
     *
     * @return una lista con todas las solicitudes.
     */
    public List<SolicitudResponse> getAllSolicitudes() {
        return solicitudRepository.findAll()
                .stream()
                .map(SolicitudResponseMapper::toResponse)
                .toList();
    }

    /**
     * Devuelve una solicitud por su id.
     *
     * @param id el id de la solicitud a buscar.
     * @return la solicitud con el id especificado o null si no existe.
     * @throws IllegalArgumentException si no se encuentra la solicitud con el id especificado.
     */
    public SolicitudResponse getSolicitudById(Integer id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "SOLICITUD_NOT_FOUND",
                        "No se encontró la solicitud con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        return SolicitudResponseMapper.toResponse(solicitud);
    }


    /**
     * Convierte una solicitudRequest en una solicitudEntity.
     *
     * @param solicitudRequest la solicitud a convertir.
     * @return la solicitudEntity convertida.
     */
    @Transactional
    private Solicitud mapToEntity(SolicitudRequest solicitudRequest) {
        Solicitud solicitud = new Solicitud();
        Solicitante solicitante = solicitanteService.getSolicitanteById(solicitudRequest.getDni());
        if (solicitante == null) {
            solicitante = new Solicitante();
            solicitante.setDni(solicitudRequest.getDni());
            solicitante.setNombre(solicitudRequest.getNombreSolicitante());
            solicitante = solicitanteService.saveSolicitante(solicitante);
        }
        solicitud.setSolicitante(solicitante);
        solicitud.setImporteSolicitado(solicitudRequest.getImporteSolicitado().doubleValue());
        solicitud.setDivisa(solicitudRequest.getDivisa());
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        return solicitud;
    }

    @Transactional
    public SolicitudResponse actualizarEstado(Integer idSolicitud, UpdateEstadoSolicitudRequest req) {
        Solicitud solicitud = solicitudRepository.findById(Math.toIntExact(idSolicitud))
                .orElseThrow(() -> new ApiException("SOLICITUD_NOT_FOUND","No existe la solicitud con id " + idSolicitud,HttpStatus.NOT_FOUND));

        EstadoSolicitud estadoActual = solicitud.getEstadoSolicitud();

        if (estadoActual == req.getNuevoEstado()) {
            return SolicitudResponseMapper.toResponse(solicitud);
        }

        if (!estadoActual.transicionarA(req.getNuevoEstado())) {
            throw new ApiException(
                    "TRANSACCION_NO_PERMITIDA",
                    "Transición no permitida: " + estadoActual + " -> " + req.getNuevoEstado(),
                    HttpStatus.BAD_REQUEST
            );
        }
        solicitud.setEstadoSolicitud(req.getNuevoEstado());
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud));
    }
}
