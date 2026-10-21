package com.example.msp_bi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "integracoes")
@Getter
@Setter
public class Integracao {

    public enum Tipo {
        API,
        WEBHOOK
    }

    public enum Autenticacao {
        NENHUMA,
        BASIC,
        BEARER,
        OAUTH2
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O tipo da integração é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    @Size(max = 255, message = "A URL deve ter até 255 caracteres")
    @Column(nullable = true)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Autenticacao autenticacao;

    @Size(max = 100, message = "O usuário deve ter até 100 caracteres")
    @Column(nullable = true)
    private String usuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 255, message = "O segredo deve ter até 255 caracteres")
    @Column(nullable = true)
    private String segredo;

    @Size(max = 255, message = "A URL do token deve ter até 255 caracteres")
    @Column(nullable = true)
    private String tokenUrl;

    @Size(max = 255, message = "O escopo deve ter até 255 caracteres")
    @Column(nullable = true)
    private String escopo;

    @Column(nullable = true, unique = true, updatable = false)
    private String webhookToken;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String mapeamento;

    @NotNull(message = "O campo ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "fonte_dados_id")
    @JsonIgnore
    private FonteDados fonteDados;

}
