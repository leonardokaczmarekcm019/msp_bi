package com.example.msp_bi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarefa") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = true, length = 300)
    private String descricao;

    @Column(nullable = true, length = 50)
    private String prioridade;

    @Column(nullable = true)
    private Date prazo;

    @Column(nullable = true)
    private Double porcentagem_progresso;

    @Column(nullable = true)
    private Date data_criacao;
    
    @Column(nullable = true)
    private Date data_conclusao;

}