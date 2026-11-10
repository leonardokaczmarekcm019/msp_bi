package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Dispositivo;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    Optional<Dispositivo> findByNome(String nome);
}
