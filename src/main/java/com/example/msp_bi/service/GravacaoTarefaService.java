package com.example.msp_bi.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.msp_bi.model.CategoriaTarefa;
import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.QuadroTarefa;
import com.example.msp_bi.model.Tarefa;
import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.CategoriaTarefaRepository;
import com.example.msp_bi.repository.QuadroTarefaRepository;
import com.example.msp_bi.repository.TarefaRepository;

@Service
public class GravacaoTarefaService {

    private final QuadroTarefaRepository quadros;
    private final CategoriaTarefaRepository categorias;
    private final TarefaRepository tarefas;
    private final GravacaoUsuarioService gravacaoUsuarioService;

    public GravacaoTarefaService(
            QuadroTarefaRepository quadros,
            CategoriaTarefaRepository categorias,
            TarefaRepository tarefas,
            GravacaoUsuarioService gravacaoUsuarioService) {
        this.quadros = quadros;
        this.categorias = categorias;
        this.tarefas = tarefas;
        this.gravacaoUsuarioService = gravacaoUsuarioService;
    }

    @Transactional
    public int salvarQuadroComTarefas(FonteDados fonte, Map<String, Object> dados) {
        String id = obrigatorio(dados.get("idExterno"), "quadro.idExterno", 255);
        QuadroTarefa quadro = quadros.findByFonteDadosIdAndIdExterno(fonte.getId(), id)
                .orElseGet(QuadroTarefa::new);
        quadro.setIdExterno(id);
        quadro.setFonteDados(fonte);
        quadro.setNome(obrigatorio(dados.get("nome"), "quadro.nome", 50));
        quadro = quadros.save(quadro);

        int total = 0;
        for (Object item : lista(dados.get("categorias"), "categorias")) {
            Map<?, ?> dadosCategoria = objeto(item, "categoria");
            String idCategoria = obrigatorio(dadosCategoria.get("id"), "categoria.id", 255);
            CategoriaTarefa categoria = categorias.findByQuadroTarefaIdAndIdExterno(quadro.getId(), idCategoria)
                    .orElseGet(CategoriaTarefa::new);
            categoria.setIdExterno(idCategoria);
            categoria.setNome(obrigatorio(dadosCategoria.get("nome"), "categoria.nome", 50));
            categoria.setQuadroTarefa(quadro);
            categoria = categorias.save(categoria);

            for (Object itemTarefa : lista(dadosCategoria.get("tarefas"), "tarefas")) {
                salvarTarefa(fonte, categoria, objeto(itemTarefa, "tarefa"));
                total++;
            }
        }
        return total;
    }

    private void salvarTarefa(FonteDados fonte, CategoriaTarefa categoria, Map<?, ?> dados) {
        String id = obrigatorio(dados.get("id"), "tarefa.id", 255);
        Tarefa tarefa = tarefas.findByCategoriaTarefaQuadroTarefaFonteDadosIdAndIdExterno(fonte.getId(), id)
                .orElseGet(Tarefa::new);
        tarefa.setIdExterno(id);
        tarefa.setCategoriaTarefa(categoria);
        tarefa.setNome(obrigatorio(dados.get("nome"), "tarefa.nome", 50));
        tarefa.setDescricao(opcional(dados.get("descricao"), "tarefa.descricao", 100));
        tarefa.setPrioridade(opcional(dados.get("prioridade"), "tarefa.prioridade", 50));
        tarefa.setPrazo(data(dados.get("prazo"), "tarefa.prazo"));
        tarefa.setData_criacao(dataObrigatoria(dados.get("data_criacao"), "tarefa.data_criacao"));
        tarefa.setData_conclusao(data(dados.get("data_conclusao"), "tarefa.data_conclusao"));
        Object progresso = dados.get("porcentagem_progresso");
        if (progresso != null && (!(progresso instanceof Number numero)
                || !Double.isFinite(numero.doubleValue())
                || numero.doubleValue() < 0 || numero.doubleValue() > 100)) {
            throw new IllegalArgumentException("tarefa.porcentagem_progresso deve ser um número entre 0 e 100");
        }
        tarefa.setPorcentagem_progresso(progresso == null ? null : ((Number) progresso).doubleValue());
        Usuario responsavel = gravacaoUsuarioService.salvarUsuario(objeto(dados.get("responsavel"), "tarefa.responsavel"));
        tarefa.getTecnicos().clear();
        tarefa.getTecnicos().add(responsavel);
        tarefas.save(tarefa);
    }

    private Map<?, ?> objeto(Object valor, String campo) {
        if (valor instanceof Map<?, ?> mapa) {
            return mapa;
        }
        throw new IllegalArgumentException(campo + " deve ser um objeto");
    }

    private List<?> lista(Object valor, String campo) {
        if (valor instanceof List<?> itens) {
            return itens;
        }
        throw new IllegalArgumentException(campo + " deve ser uma lista");
    }

    private String obrigatorio(Object valor, String campo, int maximo) {
        String texto = opcional(valor, campo, maximo);
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório");
        }
        return texto;
    }

    private String opcional(Object valor, String campo, int maximo) {
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof String) && !(valor instanceof Number)) {
            throw new IllegalArgumentException(campo + " deve ser texto ou número");
        }
        String texto = valor.toString();
        if (texto.length() > maximo) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + maximo + " caracteres");
        }
        return texto;
    }

    private Date dataObrigatoria(Object valor, String campo) {
        Date data = data(valor, campo);
        if (data == null) {
            throw new IllegalArgumentException(campo + " é obrigatório");
        }
        return data;
    }

    private Date data(Object valor, String campo) {
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof String texto)) {
            throw new IllegalArgumentException(campo + " deve ser uma data ISO 8601");
        }
        try {
            Instant instante;
            if (texto.length() == 10) {
                instante = LocalDate.parse(texto).atStartOfDay().toInstant(ZoneOffset.UTC);
            } else {
                try {
                    instante = OffsetDateTime.parse(texto).toInstant();
                } catch (DateTimeParseException e) {
                    instante = LocalDateTime.parse(texto).toInstant(ZoneOffset.UTC);
                }
            }
            return Date.from(instante);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(campo + " deve ser uma data ISO 8601 válida", e);
        }
    }
}
