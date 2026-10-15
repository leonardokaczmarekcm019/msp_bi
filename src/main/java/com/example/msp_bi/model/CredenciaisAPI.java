package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;

@Entity 
public class CredenciaisAPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O tipo de autenticação é obrigatório")
    @Size(max = 50, message = "O tipo de autenticação deve ter até 50 caracteres")
    @Column(nullable = false)
    private String tipo_autenticacao;

    @Size(max = 100, message = "O usuário deve ter até 100 caracteres")
    @Column(nullable = true)
    private String usuario;

    @Size(max = 100, message = "A senha deve ter até 100 caracteres")
    @Column(nullable = true)
    private String senha;

    @Size(max = 100, message = "A chave API deve ter até 100 caracteres")
    @Column(nullable = true)
    private String chave_api;

    @NotBlank(message = "O campo ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;
    
}
