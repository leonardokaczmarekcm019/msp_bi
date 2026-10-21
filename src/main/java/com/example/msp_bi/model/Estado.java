package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter
@Setter
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A sigla é obrigatória")
    @Size (max = 2, message = "A sigla deve ter até 2 caracteres")
    @Column(nullable = false, length = 2)
    private String sigla;
    
}
