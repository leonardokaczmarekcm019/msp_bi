package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Modelo;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    Optional<Modelo> findByNome(String nome);
}
