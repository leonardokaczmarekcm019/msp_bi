package com.example.msp_bi.repository;

import com.example.msp_bi.model.CredenciaisAPI;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredenciaisAPIRepository extends JpaRepository<CredenciaisAPI, Long> {
    
}