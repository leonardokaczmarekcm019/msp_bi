package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Fabricante;

public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
    Optional<Fabricante> findByNome(String nome);
}
