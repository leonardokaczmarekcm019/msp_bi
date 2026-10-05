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
import com.example.msp_bi.repository.UsuarioRepository;
import com.example.msp_bi.repository.TarefaRepository;
import com.example.msp_bi.model.Usuario;    

import jakarta.validation.Valid;




@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioRepository repository;
    private final TarefaRepository tarefaRepository;

    public UsuarioController(
            UsuarioRepository repository,
            TarefaRepository tarefaRepository) {

        this.repository = repository;
        this.tarefaRepository = tarefaRepository;
    }


    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody Usuario usuario) {

        usuario.setId(null);

        Usuario salvo = repository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{usuarioId}/tarefa/{tarefaId}")
    public ResponseEntity<Usuario> adicionarTarefa(
            @PathVariable Long usuarioId,
            @PathVariable Long tarefaId) {

        var usuario = repository.findById(usuarioId);
        var tarefa = tarefaRepository.findById(tarefaId);

        if (usuario.isEmpty() || tarefa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuario.get().setTarefa(tarefa.get());

        Usuario atualizado = repository.save(usuario.get());

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
        


}

