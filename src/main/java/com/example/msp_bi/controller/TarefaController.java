package com.example.msp_bi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Tarefa;
import com.example.msp_bi.repository.TarefaRepository;
import com.example.msp_bi.repository.UsuarioRepository;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public TarefaController(
            TarefaRepository repository,
            UsuarioRepository usuarioRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Tarefa> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Tarefa> cadastrar(
            @RequestBody Tarefa tarefa) {

        tarefa.setId(null);

        Tarefa salvo = repository.save(tarefa);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salvo);
    }

    @PutMapping("/{tarefaId}/usuario/{usuarioId}")
    public ResponseEntity<Tarefa> adicionarUsuario(
            @PathVariable Long tarefaId,
            @PathVariable Long usuarioId) {

        var tarefa = repository.findById(tarefaId);
        var usuario = usuarioRepository.findById(usuarioId);

        if (tarefa.isEmpty() || usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        tarefa.get().getTecnicos().add(usuario.get());

        Tarefa atualizado = repository.save(tarefa.get());

        return ResponseEntity.ok(atualizado);
    }
}