package com.example.msp_bi.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.Integracao;
import com.example.msp_bi.repository.IntegracaoRepository;

@Service
public class WebhookService {

    private final IntegracaoRepository integracaoRepository;
    private final GravacaoService gravacaoService;

    public WebhookService(IntegracaoRepository integracaoRepository,
            GravacaoService gravacaoService) {
        this.integracaoRepository = integracaoRepository;
        this.gravacaoService = gravacaoService;
    }

    // retorna uma mensagem de erro, ou null se deu certo
    public String receber(String token, Map<String, Object> dados) {
        var achada = integracaoRepository.findByWebhookToken(token);

        if (achada.isEmpty()) {
            return "Token inválido";
        }

        Integracao integracao = achada.get();
        FonteDados fonte = integracao.getFonteDados();

        if (!Boolean.TRUE.equals(integracao.getAtivo())
                || !Boolean.TRUE.equals(fonte.getAtivo())
                || !Boolean.TRUE.equals(fonte.getOrganizacao().getAtivo())) {
            return "Integração, fonte ou organização inativa";
        }

        if (!gravacaoService.salvarChamado(fonte, dados)) {
            return "Dados inválidos";
        }

        return null;
    }
}