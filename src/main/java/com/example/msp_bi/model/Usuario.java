package com.example.msp_bi.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size (min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100) 
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Size (min = 3, max = 100, message = "O email deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100) 
    private String email;

    @NotBlank(message = "O número é obrigatório")
    @Size (min = 3, max = 15, message = "O número deve ter entre 3 e 15 caracteres")
    @Column(nullable = false, length = 15) 
    private String numero;

    @Column(nullable = false)
    private Boolean ativo;
        
}
