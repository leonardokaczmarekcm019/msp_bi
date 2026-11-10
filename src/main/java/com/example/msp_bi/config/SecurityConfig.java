package com.example.msp_bi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filtroDeSeguranca(
            HttpSecurity http,
            AutenticacaoPrototipo autenticacaoPrototipo) throws Exception {

        return http
                .authenticationManager(new ProviderManager(autenticacaoPrototipo))
                .sessionManagement(sessao ->
                        sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(
                                "/usuarios", "/usuarios/**",
                                "/tipos-usuario", "/tipos-usuario/**",
                                "/organizacoes", "/organizacoes/**",
                                "/fontes-dados", "/fontes-dados/**",
                                "/coleta/configuracao"
                        ).hasRole("GERENTE")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}