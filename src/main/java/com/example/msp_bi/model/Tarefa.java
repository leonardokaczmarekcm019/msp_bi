package com.example.msp_bi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tarefa") 
@Getter
@Setter
@NoArgsConstructor
public class Tarefa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter até 50 caracteres")
    @Column(nullable = false)
    private String nome;

    @Size(max = 100, message = "A descrição deve ter até 100 caracteres")
    @Column(nullable = true)
    private String descricao;

    @Size(max = 50, message = "A prioridade deve ter até 50 caracteres")
    @Column(nullable = true)
    private String prioridade;

    @Column(nullable = true)
    private Date prazo;

    @Column(nullable = true)
    private Double porcentagem_progresso;

    @NotBlank(message = "O campo data de criação é obrigatório")
    @Column(nullable = false)
    private Date data_criacao;
    
    @Column(nullable = true)
    private Date data_conclusao;

    @ManyToMany
    @JoinTable(
        name = "tecnico_tarefa",
        joinColumns = @JoinColumn(name = "tarefa_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnore
    private List<Usuario> tecnicos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "categoria_tarefa_id")
    private CategoriaTarefa categoriaTarefa;

}