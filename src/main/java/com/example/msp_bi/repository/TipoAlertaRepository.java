package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.TipoAlerta;

public interface TipoAlertaRepository extends JpaRepository<TipoAlerta, Long> {
    Optional<TipoAlerta> findByNome(String nome);
}
