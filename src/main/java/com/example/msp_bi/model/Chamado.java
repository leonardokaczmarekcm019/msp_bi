package com.example.msp_bi.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "tipo_chamado_id")
    private TipoChamado tipoChamado;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaChamado categoriaChamado;

    @ManyToOne
    @JoinColumn(name = "prioridade_chamado_id")
    private PrioridadeChamado prioridadeChamado;

    @ManyToOne
    @JoinColumn(name = "fonte_dados_id")
    private FonteDados fonteDados;

    @OneToMany(mappedBy = "chamado")
    @JsonIgnore
    private List<ChamadoTecnico> tecnicos;

}