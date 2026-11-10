package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.CategoriaTarefa;

public interface CategoriaTarefaRepository extends JpaRepository<CategoriaTarefa, Long> {
    Optional<CategoriaTarefa> findByQuadroTarefaIdAndIdExterno(Long quadroId, String idExterno);
}
