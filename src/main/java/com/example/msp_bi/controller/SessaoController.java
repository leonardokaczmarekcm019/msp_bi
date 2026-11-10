package com.example.msp_bi.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessaoController {

    @GetMapping("/sessao")
    public Map<String, Object> sessao(Authentication autenticacao) {
        boolean gerente = autenticacao.getAuthorities().stream()
                .anyMatch(permissao ->
                        permissao.getAuthority().equals("ROLE_GERENTE"));

        return Map.of(
                "email", autenticacao.getName(),
                "gerente", gerente
        );
    }
}