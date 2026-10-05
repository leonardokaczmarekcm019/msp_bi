package com.example.msp_bi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.Chamado;

public interface ChamadoRepository
        extends JpaRepository<Chamado, Long> {
}