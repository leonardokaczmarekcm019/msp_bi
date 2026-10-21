package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

@Entity 
@Getter
@Setter
public class FonteDados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo plataforma é obrigatório")
    @Size(max = 50, message = "A plataforma deve ter até 50 caracteres")
    @Column(nullable = false)
    private String plataforma;

    public enum TipoFonte {
        CHAMADO,
        ALERTA,
        TAREFA
    }

    @NotNull(message = "O tipo da fonte é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TipoFonte tipoFonte;

    @Size(max = 100, message = "A URL de acesso deve ter até 100 caracteres")
    @Column(nullable = true)
    private String url;

    @NotNull(message = "O campo ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "organizacao_id")
    @JsonIgnoreProperties("fontesDados")
    private Organizacao organizacao;

}
