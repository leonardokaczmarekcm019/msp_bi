package com.example.msp_bi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chamado_tecnico")
public class ChamadoTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico;

    @ManyToOne
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    @Column(nullable = true)
    private Double apontamento;

}
