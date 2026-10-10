package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity 
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToOne
    @JoinColumn(nullable = false)
    private Bairro bairro;

    @OneToOne
    @JoinColumn(nullable = false)
    private Cidade cidade;

    @OneToOne
    @JoinColumn(nullable = false)
    private Estado estado;
    
}
