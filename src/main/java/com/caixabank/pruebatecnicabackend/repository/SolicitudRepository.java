package com.caixabank.pruebatecnicabackend.repository;

import com.caixabank.pruebatecnicabackend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    public Solicitud findBySolicitanteIdAndDivisa(String solicitanteId, String divisa);
}
