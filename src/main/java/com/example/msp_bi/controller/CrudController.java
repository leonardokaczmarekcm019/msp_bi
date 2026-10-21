package com.example.msp_bi.controller;

import java.util.List;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

public abstract class CrudController<T> {

    private final JpaRepository<T, Long> repository;

    protected CrudController(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<T> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<T> cadastrar(@Valid @RequestBody T entidade) {
        definirId(entidade, null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(entidade));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> atualizar(@PathVariable Long id, @Valid @RequestBody T entidade) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        definirId(entidade, id);

        return ResponseEntity.ok(repository.save(entidade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private void definirId(T entidade, Long id) {
        new BeanWrapperImpl(entidade).setPropertyValue("id", id);
    }
}
