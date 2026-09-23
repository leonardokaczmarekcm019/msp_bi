package com.example.msp_bi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
