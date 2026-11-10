package com.example.msp_bi.repository;

import com.example.msp_bi.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    Optional<Alerta> findByFonteDadosIdAndIdExterno(Long fonteDadosId, String idExterno);
}
