package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.PrioridadeChamado;

public interface PrioridadeChamadoRepository
        extends JpaRepository<PrioridadeChamado, Long> {

    Optional<PrioridadeChamado> findByNome(String nome);
}