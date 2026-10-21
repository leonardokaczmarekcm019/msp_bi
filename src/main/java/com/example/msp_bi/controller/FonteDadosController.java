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

import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.repository.IntegracaoRepository;
import com.example.msp_bi.repository.FonteDadosRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fontes-dados")
public class FonteDadosController {

    private final FonteDadosRepository repository;
    private final IntegracaoRepository integracaoRepository;

    public FonteDadosController(
            FonteDadosRepository repository,
            IntegracaoRepository integracaoRepository) {

        this.repository = repository;
        this.integracaoRepository = integracaoRepository;
    }

    @GetMapping
    public List<FonteDados> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FonteDados> buscar(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FonteDados> cadastrar(
            @Valid @RequestBody FonteDados fonteDados) {

        fonteDados.setId(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(fonteDados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FonteDados> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FonteDados fonteDados) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        fonteDados.setId(id);

        return ResponseEntity.ok(repository.save(fonteDados));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        integracaoRepository.deleteAll(integracaoRepository.findByFonteDadosId(id));
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
