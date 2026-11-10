package com.example.msp_bi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);
    
}
