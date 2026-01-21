package com.caixabank.pruebatecnicabackend.service;

import com.caixabank.pruebatecnicabackend.dto.request.SolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.request.UpdateEstadoSolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.response.SolicitudResponse;
import com.caixabank.pruebatecnicabackend.model.EstadoSolicitud;
import com.caixabank.pruebatecnicabackend.model.Solicitante;
import com.caixabank.pruebatecnicabackend.model.Solicitud;
import com.caixabank.pruebatecnicabackend.repository.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolicitudServiceTest {
    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private SolicitanteService solicitanteService;

    @InjectMocks
    private SolicitudService solicitudService;

    private static SolicitudRequest buildRequest(String dni, String nombre, double importe, String divisa) {
        SolicitudRequest req = new SolicitudRequest();
        req.setDni(dni);
        req.setNombreSolicitante(nombre);
        req.setImporteSolicitado(BigDecimal.valueOf(importe));
        req.setDivisa(divisa);
        return req;
    }

    private static Solicitante buildSolicitante(String dni, String nombre) {
        Solicitante s = new Solicitante();
        s.setDni(dni);
        s.setNombre(nombre);
        return s;
    }

    private static Solicitud buildSolicitud(int id, Solicitante solicitante, double importe, String divisa, EstadoSolicitud estado) {
        Solicitud sol = new Solicitud();
        sol.setId(id);
        sol.setSolicitante(solicitante);
        sol.setImporteSolicitado(importe);
        sol.setDivisa(divisa);
        sol.setEstadoSolicitud(estado);
        sol.setFechaCreacion(LocalDateTime.of(2025, 1, 1, 10, 0));
        return sol;
    }

    @Test
    void crearSolicitud_whenSolicitanteNotExists_createsAndSavesSolicitante() {
        SolicitudRequest req = buildRequest("12345678A", "Juan", 1500.50, "EUR");

        when(solicitudRepository.findBySolicitanteDniAndDivisaAndImporteSolicitado(
                req.getDni(), req.getDivisa(), req.getImporteSolicitado().doubleValue()
        )).thenReturn(null);

        when(solicitanteService.getSolicitanteById(req.getDni())).thenReturn(null);

        Solicitante savedSolicitante = buildSolicitante(req.getDni(), req.getNombreSolicitante());
        when(solicitanteService.saveSolicitante(any(Solicitante.class))).thenReturn(savedSolicitante);

        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(inv -> {
            Solicitud saved = inv.getArgument(0);
            saved.setId(77);
            return saved;
        });

        SolicitudResponse response = solicitudService.crearSolicitud(req);

        assertThat(response.getDni()).isEqualTo(req.getDni());
        assertThat(response.getNombreSolicitante()).isEqualTo(req.getNombreSolicitante());
        assertThat(response.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.PENDIENTE);

        ArgumentCaptor<Solicitante> solicitanteCaptor = ArgumentCaptor.forClass(Solicitante.class);
        verify(solicitanteService).saveSolicitante(solicitanteCaptor.capture());
        assertThat(solicitanteCaptor.getValue().getDni()).isEqualTo(req.getDni());
        assertThat(solicitanteCaptor.getValue().getNombre()).isEqualTo(req.getNombreSolicitante());
    }

    @Test
    void getAllSolicitudes_mapsAllEntitiesToResponses() {
        Solicitante s1 = buildSolicitante("1", "A");
        Solicitante s2 = buildSolicitante("2", "B");
        when(solicitudRepository.findAll()).thenReturn(List.of(
                buildSolicitud(1, s1, 10.0, "EUR", EstadoSolicitud.PENDIENTE),
                buildSolicitud(2, s2, 20.0, "USD", EstadoSolicitud.APROBADA)
        ));

        List<SolicitudResponse> result = solicitudService.getAllSolicitudes();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDni()).isEqualTo("1");
        assertThat(result.get(1).getDni()).isEqualTo("2");
        verify(solicitudRepository).findAll();
    }

    @Test
    void getSolicitudById_whenFound_returnsMappedResponse() {
        Solicitante s = buildSolicitante("123", "Juan");
        Solicitud entity = buildSolicitud(5, s, 99.9, "EUR", EstadoSolicitud.PENDIENTE);
        when(solicitudRepository.findById(5)).thenReturn(Optional.of(entity));

        SolicitudResponse result = solicitudService.getSolicitudById(5);

        assertThat(result.getDni()).isEqualTo("123");
        assertThat(result.getNombreSolicitante()).isEqualTo("Juan");
        assertThat(result.getDivisa()).isEqualTo("EUR");
        assertThat(result.getImporteSolicitado()).isEqualByComparingTo("99.9");
        assertThat(result.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.PENDIENTE);
    }

    @Test
    void actualizarEstado_whenTransitionAllowed_updatesAndSaves() {
        Solicitante s = buildSolicitante("1", "A");
        Solicitud entity = buildSolicitud(1, s, 10.0, "EUR", EstadoSolicitud.PENDIENTE);
        when(solicitudRepository.findById(1)).thenReturn(Optional.of(entity));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateEstadoSolicitudRequest req = new UpdateEstadoSolicitudRequest();
        req.setNuevoEstado(EstadoSolicitud.APROBADA);

        SolicitudResponse resp = solicitudService.actualizarEstado(1, req);

        assertThat(resp.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.APROBADA);
        verify(solicitudRepository).save(entity);
        assertThat(entity.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.APROBADA);
    }
}
