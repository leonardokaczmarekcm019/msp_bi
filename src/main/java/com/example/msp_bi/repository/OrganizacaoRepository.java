package com.example.msp_bi.repository;

import com.example.msp_bi.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {
    
}
