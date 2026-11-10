package com.example.msp_bi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Tarefa;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Optional<Tarefa> findByCategoriaTarefaQuadroTarefaFonteDadosIdAndIdExterno(
            Long fonteDadosId, String idExterno);
}