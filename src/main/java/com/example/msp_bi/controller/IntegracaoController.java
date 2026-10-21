package com.example.msp_bi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Integracao;
import com.example.msp_bi.repository.FonteDadosRepository;
import com.example.msp_bi.repository.IntegracaoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fontes-dados/{fonteDadosId}/integracoes")
public class IntegracaoController {

    private final IntegracaoRepository repository;
    private final FonteDadosRepository fonteDadosRepository;

    public IntegracaoController(
            IntegracaoRepository repository,
            FonteDadosRepository fonteDadosRepository) {

        this.repository = repository;
        this.fonteDadosRepository = fonteDadosRepository;
    }

    @GetMapping
    public List<Integracao> listar(@PathVariable Long fonteDadosId) {
        return repository.findByFonteDadosId(fonteDadosId);
    }

    @PostMapping
    public ResponseEntity<Integracao> cadastrar(
            @PathVariable Long fonteDadosId,
            @Valid @RequestBody Integracao integracao) {

        var fonte = fonteDadosRepository.findById(fonteDadosId);

        if (fonte.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        integracao.setId(null);
        integracao.setWebhookToken(null);
        integracao.setFonteDados(fonte.get());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(integracao));
    }

    // segredo é write-only: se vier vazio, mantém o já salvo
    @PutMapping("/{id}")
    public ResponseEntity<Integracao> atualizar(
            @PathVariable Long fonteDadosId,
            @PathVariable Long id,
            @Valid @RequestBody Integracao integracao) {

        var existente = repository.findById(id);

        if (existente.isEmpty()
                || !existente.get().getFonteDados().getId().equals(fonteDadosId)) {
            return ResponseEntity.notFound().build();
        }

        Integracao atual = existente.get();
        atual.setTipo(integracao.getTipo());
        atual.setUrl(integracao.getUrl());
        atual.setAutenticacao(integracao.getAutenticacao());
        atual.setUsuario(integracao.getUsuario());
        atual.setTokenUrl(integracao.getTokenUrl());
        atual.setEscopo(integracao.getEscopo());
        atual.setMapeamento(integracao.getMapeamento());
        atual.setAtivo(integracao.getAtivo());

        if (integracao.getSegredo() != null) {
            atual.setSegredo(integracao.getSegredo());
        }

        return ResponseEntity.ok(repository.save(atual));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long fonteDadosId,
            @PathVariable Long id) {

        var existente = repository.findById(id);

        if (existente.isEmpty()
                || !existente.get().getFonteDados().getId().equals(fonteDadosId)) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(existente.get());

        return ResponseEntity.noContent().build();
    }
}
