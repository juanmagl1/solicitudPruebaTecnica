package com.caixabank.pruebatecnicabackend.controller;

import com.caixabank.pruebatecnicabackend.dto.request.SolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.request.UpdateEstadoSolicitudRequest;
import com.caixabank.pruebatecnicabackend.dto.response.SolicitudResponse;
import com.caixabank.pruebatecnicabackend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitud")
@RequiredArgsConstructor
public class SolicitudController {
    private final SolicitudService solicitudService;


    /**
     * Crea una solicitud.
     * @param req la solicitud a crear.
     * @return la respuesta de la solicitud creada.
     */
    @PostMapping("/create")
    public ResponseEntity<SolicitudResponse> create(@Valid @RequestBody SolicitudRequest req) {
        return ResponseEntity.ok()
                .body(solicitudService.crearSolicitud(req));
    }

    /**
     * Devuelve una lista con todas las solicitudes.
     * @return una lista con todas las solicitudes.
     */
    @GetMapping("/all")
    public ResponseEntity<List<SolicitudResponse>> all() {
        return ResponseEntity.ok()
                .body(solicitudService.getAllSolicitudes());
    }

    /**
     * Devuelve una solicitud por su id.
     * @param id el id de la solicitud a buscar.
     * @return la solicitud con el id especificado o null si no existe.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<SolicitudResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .body(solicitudService.getSolicitudById(id));
    }

    @PatchMapping("/update/id/{id}")
    public ResponseEntity<SolicitudResponse> updateState(@PathVariable Integer id, @RequestBody UpdateEstadoSolicitudRequest req) {
        return ResponseEntity.ok()
                .body(solicitudService.actualizarEstado(id, req));
    }
}
