package com.example.msp_bi.model;
    
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String mensagem;

    @Column(nullable = true)
    private String prioridade;

    @Column(nullable = true)
    private String severidade;

    @Column(nullable = false)
    private Date data;

    @Column(nullable = false)
    private Boolean notificacao;

    @OneToOne
    @JoinColumn(nullable = true)
    private Dispositivo dispositivo;

    @OneToOne
    @JoinColumn(nullable = true)
    private ServicoRede servicoRede;

}
