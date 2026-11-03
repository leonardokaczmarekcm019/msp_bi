package com.example.msp_bi.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.msp_bi.model.Chamado;
import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.repository.ChamadoRepository;

@Service
public class GravacaoService {

    private final ChamadoRepository chamadoRepository;

    public GravacaoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    // retorna false se os dados forem inválidos (descartados)
    public boolean salvarChamado(FonteDados fonte, Map<String, Object> dados) {
        String idExterno = texto(dados.get("idExterno"));
        String titulo = texto(dados.get("titulo"));
        String url = texto(dados.get("url"));
        String descricao = texto(dados.get("descricao"));

        if (idExterno == null || titulo == null || url == null) {
            return false;
        }

        Chamado chamado = chamadoRepository
                .findByFonteDadosIdAndIdExterno(fonte.getId(), idExterno)
                .orElseGet(Chamado::new);

        chamado.setFonteDados(fonte);
        chamado.setIdExterno(idExterno);
        chamado.setTitulo(cortar(titulo, 100));
        chamado.setUrl(cortar(url, 100));
        chamado.setDescricao(descricao == null ? null : cortar(descricao, 250));

        chamadoRepository.save(chamado);
        return true;
    }

    private String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }
        return valor.toString();
    }

    private String cortar(String valor, int max) {
        return valor.length() > max ? valor.substring(0, max) : valor;
    }
}