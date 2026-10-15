package com.example.msp_bi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity 
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    @Size(max = 100, message = "A descrição deve ter até 100 caracteres")
    @Column(nullable = false)
    private String descricao;

    @NotBlank(message = "O campo logradouro é obrigatório")
    @Size(max = 100, message = "O logradouro deve ter até 100 caracteres")
    @Column(nullable = false)
    private String logradouro;

    @NotBlank(message = "O campo número é obrigatório")
    @Size(max = 50, message = "O número deve ter até 50 caracteres")
    @Column(nullable = false)
    private String numero;

    @OneToOne
    @JoinColumn(nullable = false)
    private Bairro bairro;

    @OneToOne
    @JoinColumn(nullable = false)
    private Cidade cidade;

    @OneToOne
    @JoinColumn(nullable = false)
    private Estado estado;

    @OneToOne 
    @JoinColumn(nullable = false)
    private Organizacao organizacao;

}
