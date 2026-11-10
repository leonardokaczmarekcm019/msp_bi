package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.QuadroTarefa;

public interface QuadroTarefaRepository extends JpaRepository<QuadroTarefa, Long> {
    Optional<QuadroTarefa> findByFonteDadosIdAndIdExterno(Long fonteDadosId, String idExterno);
}
