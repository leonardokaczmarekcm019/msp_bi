package com.example.msp_bi.controller;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.msp_bi.repository.OrganizacaoRepository;
import com.example.msp_bi.service.CorOrganizacaoService;

@RestController
public class CorOrganizacaoController {

    private static final Pattern HEX = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private final OrganizacaoRepository repository;
    private final CorOrganizacaoService corService;

    public CorOrganizacaoController(OrganizacaoRepository repository, CorOrganizacaoService corService) {
        this.repository = repository;
        this.corService = corService;
    }

    @GetMapping("/cores-organizacao")
    public Map<Long, String> cores() {
        return corService.todas();
    }

    @PutMapping("/organizacoes/{id}/cor")
    public Map<String, String> definirCor(@PathVariable Long id, @RequestBody Map<String, String> corpo) {
        String cor = corpo.get("cor");

        if (cor == null || !HEX.matcher(cor).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cor inválida. Use o formato #RRGGBB");
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada");
        }

        corService.definir(id, cor);
        return Map.of("cor", cor);
    }
}
