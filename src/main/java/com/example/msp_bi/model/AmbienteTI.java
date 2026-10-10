package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;

@Entity
public class AmbienteTI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String descricao;

    @OneToOne
    @JoinColumn(nullable = false)
    private TipoAmbienteTI tipoAmbienteTI;
    
}
