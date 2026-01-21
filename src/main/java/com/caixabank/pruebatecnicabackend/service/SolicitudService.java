package com.caixabank.pruebatecnicabackend.service;

import com.caixabank.pruebatecnicabackend.dto.request.SolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.response.SolicitudResponse;
import com.caixabank.pruebatecnicabackend.mapper.SolicitudResponseMapper;
import com.caixabank.pruebatecnicabackend.model.EstadoSolicitud;
import com.caixabank.pruebatecnicabackend.model.Solicitante;
import com.caixabank.pruebatecnicabackend.model.Solicitud;
import com.caixabank.pruebatecnicabackend.repository.SolicitudRepository;
import lombok.AllArgsConstructor;
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
     * @param solicitudRequest la solicitud a crear.
     * @return la respuesta de la solicitud creada.
     */
    public SolicitudResponse crearSolicitud(SolicitudRequest solicitudRequest) {
        Solicitud solicitud=solicitudRepository.findBySolicitanteIdAndDivisa(
                solicitudRequest.getDni(),
                solicitudRequest.getDivisa()
        );
        if (solicitud!=null && solicitud.getEstadoSolicitud()== EstadoSolicitud.PENDIENTE){
            //Ya existe una solicitud para ese solicitante y divisa
            throw new IllegalArgumentException("Ya existe una solicitud para ese solicitante y divisa y está en proceso de validación.");
        }
        Solicitud nuevaSolicitud=mapToEntity(solicitudRequest);
        solicitudRepository.save(nuevaSolicitud);
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(nuevaSolicitud));
    }

    /**
     * Devuelve una lista con todas las solicitudes.
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
     * @param id el id de la solicitud a buscar.
     * @return la solicitud con el id especificado o null si no existe.
     * @throws IllegalArgumentException si no se encuentra la solicitud con el id especificado.
     */
    public SolicitudResponse getSolicitudById(Integer id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la solicitud con id: " + id));
        return SolicitudResponseMapper.toResponse(solicitud);
    }

    /**
     * Convierte una solicitudRequest en una solicitudEntity.
     * @param solicitudRequest la solicitud a convertir.
     * @return la solicitudEntity convertida.
     */
    private Solicitud mapToEntity(SolicitudRequest solicitudRequest) {
        Solicitud solicitud = new Solicitud();
        Solicitante solicitante=solicitanteService.getSolicitanteById(solicitudRequest.getDni());
        if (solicitante==null){
            Solicitante newSolicitante=new Solicitante();
            newSolicitante.setDni(solicitudRequest.getDni());
            newSolicitante.setNombre(solicitudRequest.getNombreSolicitante());
            solicitanteService.saveSolicitante(newSolicitante);
        }
        solicitud.setSolicitante(solicitante);
        solicitud.setImporteSolicitado(solicitudRequest.getImporteSolicitado().doubleValue());
        solicitud.setDivisa(solicitudRequest.getDivisa());
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        return solicitud;
    }
}
