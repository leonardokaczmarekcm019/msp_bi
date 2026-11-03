package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.Chamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    Optional<Chamado> findByFonteDadosIdAndIdExterno(Long fonteDadosId, String idExterno);
}