package com.example.msp_bi.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @NotNull(message = "O status é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "tipo_usuario_id")
    private TipoUsuario tipoUsuario;

    @ManyToMany(mappedBy = "tecnicos")
    @JsonIgnore
    private List<Tarefa> tarefas = new ArrayList<>();

}
