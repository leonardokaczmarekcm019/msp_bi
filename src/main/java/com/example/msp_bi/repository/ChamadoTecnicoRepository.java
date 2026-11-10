package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.ChamadoTecnico;

public interface ChamadoTecnicoRepository
        extends JpaRepository<ChamadoTecnico, Long> {

    Optional<ChamadoTecnico> findByChamadoIdAndTecnicoId(
            Long chamadoId,
            Long tecnicoId);
}