package com.example.msp_bi.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.TipoUsuarioRepository;
import com.example.msp_bi.repository.UsuarioRepository;

@Service
public class GravacaoUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public GravacaoUsuarioService(
            UsuarioRepository usuarioRepository,
            TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public Usuario salvarUsuario(Map<?, ?> dados) {
        String nome = textoObrigatorio(dados.get("nome"), "nome do usuário", 100);
        String email = textoObrigatorio(dados.get("email"), "email do usuário", 100).trim();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseGet(Usuario::new);

        if (usuario.getId() == null) {
            usuario.setAtivo(true);
            usuario.setTipoUsuario(tipoUsuarioRepository.findByNome("Cliente")
                    .orElseThrow(() -> new IllegalStateException(
                            "O tipo de usuário Cliente não está cadastrado")));
        }

        usuario.setNome(nome);
        usuario.setEmail(email);

        if (dados.get("numero") != null) {
            usuario.setNumero(textoObrigatorio(dados.get("numero"), "número do usuário", 15));
        }

        return usuarioRepository.save(usuario);
    }

    private String textoObrigatorio(Object valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório");
        }

        String resultado = valor.toString();
        if (resultado.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " deve ter no máximo " + tamanhoMaximo + " caracteres");
        }
        return resultado;
    }
}
