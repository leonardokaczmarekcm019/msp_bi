package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity 
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = true)
    private String cnpj;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String telefone;

    @Column(nullable = true)
    private String logo;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "organizacao")
    private List<FonteDados> fontesDados = new ArrayList<>();

}