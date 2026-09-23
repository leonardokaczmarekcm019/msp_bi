package com.example.msp_bi.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size (max = 100, message = "O título deve ter até 100 caracteres")
    @Column(nullable = false, length = 100) 
    private String titulo;

    @NotBlank(message = "A URL é obrigatória")
    @Size (max = 100, message = "A URL deve ter até 100 caracteres")
    @Column(nullable = false, length = 100) 
    private String url;

    @Size (max = 500, message = "A descrição deve ter até 500 caracteres")
    @Column(nullable = true, length = 500) 
    private String descricao;

    @Column(nullable = false)
    private Boolean impedido;

    @Column(nullable = false)
    private Boolean suspenso;

    @Column(nullable = false)
    private Boolean excluido;

    @Column(nullable = false)
    private Boolean arquivado;

    @Column(nullable = false)
    private Date data_abertura;

    @Column(nullable = true)
    private Date data_conclusao;

    @Column(nullable = false)
    private Usuario solicitante;

    @Column(nullable = true)
    private Usuario tecnico;

}

"Um chamado é aberto por um solicitante"
"O técnico trabalha em vários chamados"