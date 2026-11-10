package com.example.msp_bi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.TipoUsuario;

public interface TipoUsuarioRepository
        extends JpaRepository<TipoUsuario, Long> {

    Optional<TipoUsuario> findByNome(String nome);
}