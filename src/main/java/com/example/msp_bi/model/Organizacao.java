package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter
@Setter
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter até 50 caracteres")
    @Column(nullable = false)
    private String nome;

    @Size(max = 50, message = "O cnpj deve ter até 50 caracteres")
    @Column(nullable = true)
    private String cnpj;

    @Size(max = 100, message = "O email deve ter até 100 caracteres")
    @Column(nullable = true)
    private String email;

    @Size(max = 50, message = "O telefone deve ter até 50 caracteres")
    @Column(nullable = true)
    private String telefone;

    @Size(max = 100, message = "O logo deve ter até 100 caracteres")
    @Column(nullable = true)
    private String logo;

    @NotNull(message = "O campo ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "organizacao")
    @JsonIgnore
    private List<FonteDados> fontesDados = new ArrayList<>();

}