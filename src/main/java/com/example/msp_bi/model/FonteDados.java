package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "O campo plataforma é obrigatório")
    @Size(max = 50, message = "A plataforma deve ter até 50 caracteres")
    @Column(nullable = false)
    private String plataforma;

    @Size(max = 100, message = "A descrição deve ter até 100 caracteres")
    @Column(nullable = true)
    private String descricao;

    @Size(max = 100, message = "A URL de acesso deve ter até 100 caracteres")
    @Column(nullable = true)
    private String url;

    @NotBlank(message = "O campo ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "organizacao_id")
    @JsonIgnore
    private Organizacao organizacao;

}
