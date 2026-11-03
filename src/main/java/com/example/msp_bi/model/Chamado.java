package com.example.msp_bi.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chamado")
@Getter
@Setter
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String idExterno;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter até 100 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "A URL é obrigatória")
    @Size(max = 100, message = "A URL deve ter até 100 caracteres")
    @Column(nullable = false)
    private String url;

    @Size(max = 250, message = "A descrição deve ter até 250 caracteres")
    @Column(nullable = true)
    private String descricao;

    @NotNull
    @Column(nullable = false)
    private Boolean impedido = false;

    @NotNull
    @Column(nullable = false)
    private Boolean suspenso = false;

    @NotNull
    @Column(nullable = false)
    private Boolean excluido = false;

    @NotNull
    @Column(nullable = false)
    private Boolean arquivado = false;

    @Column(name = "data_abertura", nullable = true)
    private Date dataAbertura;

    @Column(name = "data_conclusao", nullable = true)
    private Date dataConclusao;

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