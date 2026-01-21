package com.caixabank.pruebatecnicabackend.repository;

import com.caixabank.pruebatecnicabackend.model.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitanteRepository extends JpaRepository<Solicitante, String> {
}
