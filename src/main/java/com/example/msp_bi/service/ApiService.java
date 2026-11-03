package com.example.msp_bi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.Integracao;
import com.example.msp_bi.repository.FonteDadosRepository;
import com.example.msp_bi.repository.IntegracaoRepository;

import tools.jackson.databind.json.JsonMapper;

@Service
public class ApiService {

    private final FonteDadosRepository fonteDadosRepository;
    private final IntegracaoRepository integracaoRepository;
    private final GravacaoService gravacaoService;
    private final RestClient restClient = RestClient.create();
    private final JsonMapper jsonMapper = new JsonMapper();

    public ApiService(FonteDadosRepository fonteDadosRepository,
            IntegracaoRepository integracaoRepository,
            GravacaoService gravacaoService) {
        this.fonteDadosRepository = fonteDadosRepository;
        this.integracaoRepository = integracaoRepository;
        this.gravacaoService = gravacaoService;
    }

    // roda sozinho no intervalo definido; retorna quantos itens foram gravados
    @Scheduled(fixedDelayString = "${coleta.intervalo-ms:300000}")
    public int coletarTudo() {
        int total = 0;

        for (FonteDados fonte : fonteDadosRepository.findByAtivoTrue()) {
            boolean organizacaoAtiva = fonte.getOrganizacao() != null
                    && Boolean.TRUE.equals(fonte.getOrganizacao().getAtivo());

            if (!organizacaoAtiva || fonte.getTipoFonte() != FonteDados.TipoFonte.CHAMADO) {
                continue;
            }

            for (Integracao integracao : integracaoRepository
                    .findByFonteDadosIdAndAtivoTrue(fonte.getId())) {

                if (integracao.getTipo() == Integracao.Tipo.API) {
                    total += coletar(fonte, integracao);
                }
            }
        }

        System.out.println("Coleta finalizada. Itens gravados: " + total);
        return total;
    }

    private int coletar(FonteDados fonte, Integracao integracao) {
        int gravados = 0;

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> mapa = jsonMapper.readValue(integracao.getMapeamento(), Map.class);

            List<Map<String, Object>> itens = restClient.get()
                    .uri(integracao.getUrl())
                    .header("User-Agent", "msp-bi")
                    .headers(h -> {
                        if (integracao.getAutenticacao() == Integracao.Autenticacao.BASIC) {
                            h.setBasicAuth(integracao.getUsuario(), integracao.getSegredo());
                        } else if (integracao.getAutenticacao() == Integracao.Autenticacao.BEARER) {
                            h.setBearerAuth(integracao.getSegredo());
                        }
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (itens == null) {
                return 0;
            }

            // monta estrutura conforme mapeamento {"titulo": item.get("title"), ...} 
            for (Map<String, Object> item : itens) {
            
                Map<String, Object> dados = new HashMap<>();
                for (Map.Entry<String, String> campo : mapa.entrySet()) {
                    dados.put(campo.getKey(), item.get(campo.getValue()));
                }

                if (gravacaoService.salvarChamado(fonte, dados)) {
                    gravados++;
                } else {
                    System.out.println("Item descartado (dados inválidos): " + dados);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na integração " + integracao.getId() + ": " + e.getMessage());
        }

        return gravados;
    }
}