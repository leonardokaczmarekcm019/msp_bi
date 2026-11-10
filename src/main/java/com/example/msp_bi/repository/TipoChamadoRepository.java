package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.TipoChamado;

public interface TipoChamadoRepository
        extends JpaRepository<TipoChamado, Long> {

    Optional<TipoChamado> findByNome(String nome);
}