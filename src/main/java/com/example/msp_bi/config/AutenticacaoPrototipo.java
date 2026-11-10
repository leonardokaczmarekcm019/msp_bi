package com.example.msp_bi.config;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.msp_bi.model.TipoUsuario;
import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.UsuarioRepository;

@Component
public class AutenticacaoPrototipo implements AuthenticationProvider {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoPrototipo(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String email = authentication.getName().trim();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Acesso negado"));

        TipoUsuario tipo = usuario.getTipoUsuario();

        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || tipo == null
                || !Boolean.TRUE.equals(tipo.getAutenticacao())) {
            throw new BadCredentialsException("Acesso negado");
        }

        String permissao = "Gerente".equals(tipo.getNome())
                ? "ROLE_GERENTE"
                : "ROLE_USUARIO";

        return new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(permissao)));
    }

    @Override
    public boolean supports(Class<?> tipo) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(tipo);
    }
}