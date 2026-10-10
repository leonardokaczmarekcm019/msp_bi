package com.example.msp_bi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "chamado")
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

    @OneToOne 
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @OneToOne
    @JoinColumn(name = "tipo_chamado_id")
    private TipoChamado tipoChamado;

    @OneToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaChamado categoriaChamado;

    @OneToOne
    @JoinColumn(name = "prioridade_chamado_id")
    private PrioridadeChamado prioridadeChamado;


}