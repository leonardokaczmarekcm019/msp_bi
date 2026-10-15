package com.example.msp_bi.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter 
@NoArgsConstructor 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size (max = 100, message = "O nome deve ter até 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Size (max = 100, message = "O email deve ter até 100 caracteres")
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank(message = "O número é obrigatório")
    @Size ( max = 50, message = "O número deve ter 50 caracteres")
    @Column(nullable = false, length = 15) 
    private String numero;

    @NotBlank(message = "O status é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @OneToOne
    @JoinColumn(name = "tipo_usuario_id")
    private TipoUsuario tipoUsuario;

    @OneToMany(mappedBy = "usuario")
    private List<Tarefa> tarefas = new ArrayList<>();

}
