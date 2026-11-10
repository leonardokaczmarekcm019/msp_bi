package com.example.msp_bi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.OptionalInt;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class AgendamentoColetaService {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoColetaService.class);
    private final ApiService apiService;
    private final TaskScheduler scheduler;
    private final Path arquivoIntervalo;
    private long intervaloMs;
    private ScheduledFuture<?> agendamento;
    private Instant proximaColeta;
    private boolean sincronizando;
    private boolean encerrando;
    private long versaoAgendamento;
    private long coletasConcluidas;

    public record Estado(boolean sincronizando, Long proximaEmMs, long coletasConcluidas) {
    }

    public AgendamentoColetaService(ApiService apiService, TaskScheduler scheduler,
            @Value("${coleta.intervalo-ms:300000}") long intervaloMs,
            @Value("${coleta.arquivo-intervalo:dados/intervalo-coleta.txt}") String arquivoIntervalo) {
        this.apiService = apiService;
        this.scheduler = scheduler;
        this.arquivoIntervalo = Path.of(arquivoIntervalo);
        this.intervaloMs = intervaloMs;
        if (Files.exists(this.arquivoIntervalo)) {
            try {
                this.intervaloMs = Long.parseLong(Files.readString(this.arquivoIntervalo).trim());
            } catch (IOException | NumberFormatException e) {
                throw new IllegalStateException("Não foi possível ler o intervalo da coleta", e);
            }
        }
        if (this.intervaloMs <= 0) {
            throw new IllegalArgumentException("O intervalo da coleta deve ser maior que zero");
        }
    }

    public synchronized double intervaloMinutos() {
        return intervaloMs / 60000.0;
    }

    public synchronized void definirIntervalo(int minutos) {
        if (minutos < 1) {
            throw new IllegalArgumentException("O intervalo deve ser de pelo menos um minuto");
        }
        long novoIntervaloMs = minutos * 60000L;
        try {
            Path diretorio = arquivoIntervalo.getParent();
            if (diretorio != null) {
                Files.createDirectories(diretorio);
            }
            Files.writeString(arquivoIntervalo, Long.toString(novoIntervaloMs));
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar o intervalo da coleta", e);
        }
        intervaloMs = novoIntervaloMs;
        if (agendamento != null) {
            agendamento.cancel(false);
            agendamento = null;
            agendar(Instant.now().plusMillis(intervaloMs));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void iniciar() {
        if (!sincronizando && agendamento == null && !encerrando) {
            agendar(Instant.now());
        }
    }

    public synchronized Estado estado() {
        Long restante = proximaColeta == null ? null
                : Math.max(0, proximaColeta.toEpochMilli() - System.currentTimeMillis());
        return new Estado(sincronizando, restante, coletasConcluidas);
    }

    public OptionalInt executarManual() {
        synchronized (this) {
            if (sincronizando || encerrando) {
                return OptionalInt.empty();
            }
            if (agendamento != null) {
                agendamento.cancel(false);
                agendamento = null;
            }
            versaoAgendamento++;
            sincronizando = true;
            proximaColeta = null;
        }
        return OptionalInt.of(executar());
    }

    private void executarAgendada(long versao) {
        synchronized (this) {
            if (versao != versaoAgendamento || sincronizando || encerrando) {
                return;
            }
            agendamento = null;
            sincronizando = true;
            proximaColeta = null;
        }
        try {
            executar();
        } catch (RuntimeException e) {
            log.error("Erro na coleta agendada", e);
        }
    }

    private int executar() {
        try {
            return apiService.coletarTudo();
        } finally {
            synchronized (this) {
                sincronizando = false;
                coletasConcluidas++;
                if (!encerrando) {
                    agendar(Instant.now().plusMillis(intervaloMs));
                }
            }
        }
    }

    private void agendar(Instant horario) {
        long versao = ++versaoAgendamento;
        agendamento = scheduler.schedule(() -> executarAgendada(versao), horario);
        proximaColeta = horario;
    }

    @PreDestroy
    public synchronized void encerrar() {
        encerrando = true;
        versaoAgendamento++;
        if (agendamento != null) {
            agendamento.cancel(false);
        }
        agendamento = null;
        proximaColeta = null;
    }
}
