package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.CategoriaChamado;

public interface CategoriaChamadoRepository
        extends JpaRepository<CategoriaChamado, Long> {

    Optional<CategoriaChamado> findByNome(String nome);
}