package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;
import jakarta.persistence.Id;

import jakarta.persistence.Entity;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String mensagem;

    @Column(nullable = true)
    private String prioridade;

    @Column(nullable = true)
    private String severidade;

    @Column(nullable = true)
    private Date data;

    @Column(nullable = true)
    private Boolean notificacao;

}
