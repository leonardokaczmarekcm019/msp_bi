package com.example.msp_bi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.UsuarioRepository;
import com.example.msp_bi.repository.TipoUsuarioRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public UsuarioController(
            UsuarioRepository repository,
            TipoUsuarioRepository tipoUsuarioRepository) {
        this.repository = repository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody Usuario usuario) {

        usuario.setId(null);
        validarEmailUnico(usuario);

        usuario.setTipoUsuario(
                tipoUsuarioRepository.findByNome("Cliente")
                        .orElseThrow(() -> new IllegalStateException(
                                "O tipo de usuário Cliente não está cadastrado"))
        );

        Usuario salva = repository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuario.setId(id);
        validarEmailUnico(usuario);

        return ResponseEntity.ok(repository.save(usuario));
    }

    private void validarEmailUnico(Usuario usuario) {
        String email = usuario.getEmail().trim();
        repository.findByEmailIgnoreCase(email)
                .filter(existente -> !existente.getId().equals(usuario.getId()))
                .ifPresent(existente -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
                });
        usuario.setEmail(email);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, List<String>>> erroDeCadastro(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode())
                .body(Map.of("errors", List.of(erro.getReason())));
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
