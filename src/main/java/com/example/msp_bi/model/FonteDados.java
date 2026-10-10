package com.example.msp_bi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity 
public class FonteDados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plataforma;

    @Column(nullable = true)
    private String descricao;

    @Column(nullable = true)
    private String chave_api;

    @Column(nullable = true)
    private String url_acesso;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "organizacao_id")
    @JsonIgnore
    private Organizacao organizacao;

}
