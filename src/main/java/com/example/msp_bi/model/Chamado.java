package com.example.msp_bi.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
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
    @Column(nullable = false) 
    private String titulo;

    @NotBlank(message = "A URL é obrigatória")
    @Size (max = 100, message = "A URL deve ter até 100 caracteres")
    @Column(nullable = false) 
    private String url;

    @Size (max = 250, message = "A descrição deve ter até 250 caracteres")
    @Column(nullable = true) 
    private String descricao;

    @NotBlank(message = "O campo impedido é obrigatório")
    @Column(nullable = false)
    private Boolean impedido;

    @NotBlank(message = "O campo suspenso é obrigatório")
    @Column(nullable = false)
    private Boolean suspenso;

    @NotBlank(message = "O campo excluído é obrigatório")
    @Column(nullable = false)
    private Boolean excluido;

    @NotBlank(message = "O campo arquivado é obrigatório")
    @Column(nullable = false)
    private Boolean arquivado;

    @NotBlank(message = "O campo data de abertura é obrigatório")
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

    @ManyToMany
    @JoinTable(
        name = "tecnico_chamado",
        joinColumns = @JoinColumn(name = "chamado_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> tecnicos;

}