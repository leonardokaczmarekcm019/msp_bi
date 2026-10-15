package com.example.msp_bi.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "O campo mensagem é obrigatório")
    @Size(max = 250, message = "A mensagem deve ter até 250 caracteres")
    @Column(nullable = false)
    private String mensagem;

    @NotBlank(message = "O campo prioridade é obrigatório")
    @Size(max = 50, message = "A prioridade deve ter até 50 caracteres")
    @Column(nullable = false)
    private String prioridade;

    @Size(max = 50, message = "A severidade deve ter até 50 caracteres")
    @Column(nullable = true)
    private String severidade;

    @NotBlank(message = "O campo data é obrigatório")
    @Column(nullable = false)
    private Date data;

    @NotBlank(message = "O status da notificação é obrigatório")
    @Column(nullable = false)
    private Boolean notificacao;

    @OneToOne 
    @JoinColumn(nullable = false, name = "tipo_alerta_id")
    private TipoAlerta tipoAlerta;

    @OneToOne
    @JoinColumn(nullable = true, name = "dispositivo_id")
    private Dispositivo dispositivo;

    @OneToOne
    @JoinColumn(nullable = true, name = "servico_rede_id")
    private ServicoRede servicoRede;

}
