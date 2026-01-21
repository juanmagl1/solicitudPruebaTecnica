package com.caixabank.pruebatecnicabackend.service;

import com.caixabank.pruebatecnicabackend.model.Solicitante;
import com.caixabank.pruebatecnicabackend.repository.SolicitanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolicitanteService {

    private final SolicitanteRepository solicitanteRepository;

    /**
     * Obtener un solicitante por su id.
     * @param id el id del solicitante.
     * @return el solicitante con el id especificado o null si no existe.
     */
    public Solicitante getSolicitanteById(String id) {
        return solicitanteRepository.findById(id).orElse(null);
    }

    public Solicitante saveSolicitante(Solicitante solicitante) {
        return solicitanteRepository.save(solicitante);
    }
}
