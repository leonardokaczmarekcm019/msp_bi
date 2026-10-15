package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;

@Entity 
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter até 50 caracteres")
    @Column(nullable = false)
    private String nome;

    @Size(max = 50, message = "O endereço MAC deve ter até 50 caracteres")
    @Column(nullable = true)
    private String endereco_mac;

    @Size(max = 50, message = "O endereço IP deve ter até 50 caracteres")
    @Column(nullable = true)
    private String endereco_ip;

    @Size(max = 100, message = "A URL deve ter até 100 caracteres")
    @Column(nullable = true)
    private String url;

    @OneToOne
    @JoinColumn(nullable = false)
    private Modelo modelo;

    @OneToOne
    @JoinColumn(nullable = false)
    private Fabricante fabricante;
    
}
