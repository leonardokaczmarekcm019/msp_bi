package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter
@Setter
public class TipoUsuario {

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

    @NotNull(message = "O campo autenticação é obrigatório")
    @Column(nullable = false)
    private Boolean autenticacao;
    
}
