package com.example.msp_bi.controller;

import java.math.BigDecimal;
import java.util.OptionalInt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.msp_bi.service.AgendamentoColetaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/coleta")
public class ApiController {

    public record Configuracao(
            @NotNull(message = "Informe o intervalo")
            @Min(value = 1, message = "O intervalo deve ser de pelo menos um minuto")
            @Max(value = Integer.MAX_VALUE, message = "Intervalo muito grande")
            @Digits(integer = 10, fraction = 0, message = "Informe um número inteiro de minutos")
            BigDecimal intervaloMinutos) {
    }

    private final AgendamentoColetaService service;

    public ApiController(AgendamentoColetaService service) {
        this.service = service;
    }

    @PostMapping("/executar")
    public String executar() {
        OptionalInt total = service.executarManual();
        if (total.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma coleta em andamento");
        }
        return "Itens gravados: " + total.getAsInt();
    }

    @GetMapping("/estado")
    public AgendamentoColetaService.Estado estado() {
        return service.estado();
    }

    @GetMapping("/configuracao")
    public double configuracao() {
        return service.intervaloMinutos();
    }

    @PutMapping("/configuracao")
    public void configurar(@Valid @RequestBody Configuracao configuracao) {
        service.definirIntervalo(configuracao.intervaloMinutos().intValueExact());
    }
}