package com.example.msp_bi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.Integracao;
import com.example.msp_bi.repository.FonteDadosRepository;
import com.example.msp_bi.repository.IntegracaoRepository;

import tools.jackson.databind.json.JsonMapper;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    private final FonteDadosRepository fonteDadosRepository;
    private final IntegracaoRepository integracaoRepository;
    private final GravacaoChamadoService gravacaoChamadoService;
    private final GravacaoTarefaService gravacaoTarefaService;
    private final GravacaoAlertaService gravacaoAlertaService;
    private final RestClient restClient = RestClient.create();
    private final JsonMapper jsonMapper = new JsonMapper();

    public ApiService(FonteDadosRepository fonteDadosRepository,
            IntegracaoRepository integracaoRepository,
            GravacaoChamadoService gravacaoChamadoService,
            GravacaoTarefaService gravacaoTarefaService,
            GravacaoAlertaService gravacaoAlertaService) {
        this.fonteDadosRepository = fonteDadosRepository;
        this.integracaoRepository = integracaoRepository;
        this.gravacaoChamadoService = gravacaoChamadoService;
        this.gravacaoTarefaService = gravacaoTarefaService;
        this.gravacaoAlertaService = gravacaoAlertaService;
    }

    public int coletarTudo() {
        int total = 0;
        int falhas = 0;

        for (FonteDados fonte : fonteDadosRepository.findByAtivoTrue()) {
            boolean organizacaoAtiva = fonte.getOrganizacao() != null
                    && Boolean.TRUE.equals(fonte.getOrganizacao().getAtivo());

            if (!organizacaoAtiva) {
                continue;
            }

            for (Integracao integracao : integracaoRepository
                    .findByFonteDadosIdAndAtivoTrue(fonte.getId())) {

                if (integracao.getTipo() == Integracao.Tipo.API) {
                    try {
                        total += coletar(fonte, integracao);
                    } catch (RuntimeException e) {
                        falhas++;
                        log.error("Erro na integração {}", integracao.getId(), e);
                    }
                }
            }
        }

        if (falhas > 0) {
            throw new IllegalStateException("Coleta com " + falhas
                    + " integração(ões) com erro. Itens gravados nas integrações concluídas: " + total
                    + ". Consulte o log do backend.");
        }
        log.info("Coleta finalizada. Itens gravados: {}", total);
        return total;
    }

    private int coletar(FonteDados fonte, Integracao integracao) {
        int gravados = 0;

        try {
            Map<?, ?> mapa = jsonMapper.readValue(integracao.getMapeamento(), Map.class);
            if (mapa == null) {
                throw new IllegalArgumentException("Mapeamento deve ser um objeto JSON");
            }
            MapeamentoApi.validar(mapa);

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

            for (Map<String, Object> item : itens) {
                Map<String, Object> dados = MapeamentoApi.aplicar(item, mapa);

                int salvos = switch (fonte.getTipoFonte()) {
                    case CHAMADO -> gravacaoChamadoService.salvarChamado(fonte, dados) ? 1 : 0;
                    case TAREFA -> gravacaoTarefaService.salvarQuadroComTarefas(fonte, dados);
                    case ALERTA -> gravacaoAlertaService.salvarAlerta(fonte, dados) ? 1 : 0;
                };
                gravados += salvos;
                if (salvos == 0 && fonte.getTipoFonte() == FonteDados.TipoFonte.CHAMADO) {
                    System.out.println("Item descartado: dados obrigatórios ausentes.");
                }
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("Falha na coleta da integração " + integracao.getId(), e);
        }

        return gravados;
    }
}