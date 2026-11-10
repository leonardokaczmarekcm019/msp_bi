package com.example.msp_bi.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.msp_bi.model.CategoriaChamado;
import com.example.msp_bi.model.Chamado;
import com.example.msp_bi.model.ChamadoTecnico;
import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.PrioridadeChamado;
import com.example.msp_bi.model.TipoChamado;
import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.CategoriaChamadoRepository;
import com.example.msp_bi.repository.ChamadoRepository;
import com.example.msp_bi.repository.ChamadoTecnicoRepository;
import com.example.msp_bi.repository.PrioridadeChamadoRepository;
import com.example.msp_bi.repository.TipoChamadoRepository;

@Service
public class GravacaoChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final CategoriaChamadoRepository categoriaRepository;
    private final PrioridadeChamadoRepository prioridadeRepository;
    private final TipoChamadoRepository tipoChamadoRepository;
    private final ChamadoTecnicoRepository chamadoTecnicoRepository;
    private final GravacaoUsuarioService gravacaoUsuarioService;

    public GravacaoChamadoService(
        ChamadoRepository chamadoRepository,
        CategoriaChamadoRepository categoriaRepository,
        PrioridadeChamadoRepository prioridadeRepository,
        TipoChamadoRepository tipoChamadoRepository,
        ChamadoTecnicoRepository chamadoTecnicoRepository,
        GravacaoUsuarioService gravacaoUsuarioService) {

        this.chamadoRepository = chamadoRepository;
        this.categoriaRepository = categoriaRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.tipoChamadoRepository = tipoChamadoRepository;
        this.chamadoTecnicoRepository = chamadoTecnicoRepository;
        this.gravacaoUsuarioService = gravacaoUsuarioService;
    }

    @Transactional
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

        if (dados.get("impedido") != null) {
            chamado.setImpedido(booleano(dados.get("impedido"), "impedido"));
        }
        if (dados.get("suspenso") != null) {
            chamado.setSuspenso(booleano(dados.get("suspenso"), "suspenso"));
        }
        if (dados.get("excluido") != null) {
            chamado.setExcluido(booleano(dados.get("excluido"), "excluido"));
        }
        if (dados.get("arquivado") != null) {
            chamado.setArquivado(booleano(dados.get("arquivado"), "arquivado"));
        }

        if (dados.containsKey("dataAbertura")) {
            chamado.setDataAbertura(data(dados.get("dataAbertura"), "dataAbertura"));
        }
        if (dados.containsKey("dataConclusao")) {
            chamado.setDataConclusao(data(dados.get("dataConclusao"), "dataConclusao"));
        }

        if (dados.get("solicitante") != null) {
            Usuario solicitante = gravacaoUsuarioService.salvarUsuario(
                    objeto(dados.get("solicitante"), "solicitante"));
            chamado.setSolicitante(solicitante);
        }

        if (dados.get("categoria") != null) {
            CategoriaChamado categoria = salvarCategoria(
                    objeto(dados.get("categoria"), "categoria"));
            chamado.setCategoriaChamado(categoria);
        }

        if (dados.get("prioridade") != null) {
            PrioridadeChamado prioridade = salvarPrioridade(
                    objeto(dados.get("prioridade"), "prioridade"));
            chamado.setPrioridadeChamado(prioridade);
        }

        if (dados.get("tipoChamado") != null) {
            TipoChamado tipo = salvarTipoChamado(
                    objeto(dados.get("tipoChamado"), "tipoChamado"));
            chamado.setTipoChamado(tipo);
        }

        Usuario responsavel = null;
        if (dados.get("responsavel") != null) {
            responsavel = gravacaoUsuarioService.salvarUsuario(
                    objeto(dados.get("responsavel"), "responsavel"));
        }

        chamado = chamadoRepository.save(chamado);

        if (responsavel != null) {
            vincularTecnico(chamado, responsavel);
        }

        return true;
    }

    private CategoriaChamado salvarCategoria(Map<?, ?> dados) {
        String nome = textoObrigatorio(dados.get("nome"), "nome da categoria", 50);

        CategoriaChamado categoria = categoriaRepository.findByNome(nome)
                .orElseGet(CategoriaChamado::new);

        categoria.setNome(nome);
        return categoriaRepository.save(categoria);
    }

    private PrioridadeChamado salvarPrioridade(Map<?, ?> dados) {
        String nome = textoObrigatorio(dados.get("nome"), "nome da prioridade", 50);

        PrioridadeChamado prioridade = prioridadeRepository.findByNome(nome)
                .orElseGet(PrioridadeChamado::new);

        prioridade.setNome(nome);
        return prioridadeRepository.save(prioridade);
    }

    private TipoChamado salvarTipoChamado(Map<?, ?> dados) {
        String nome = textoObrigatorio(dados.get("nome"), "nome do tipo de chamado", 50);

        TipoChamado tipo = tipoChamadoRepository.findByNome(nome)
                .orElseGet(TipoChamado::new);

        tipo.setNome(nome);
        return tipoChamadoRepository.save(tipo);
    }

    private void vincularTecnico(Chamado chamado, Usuario tecnico) {
        boolean existe = chamadoTecnicoRepository
                .findByChamadoIdAndTecnicoId(chamado.getId(), tecnico.getId())
                .isPresent();

        if (!existe) {
            ChamadoTecnico vinculo = new ChamadoTecnico();
            vinculo.setChamado(chamado);
            vinculo.setTecnico(tecnico);
            chamadoTecnicoRepository.save(vinculo);
        }
    }

    private Map<?, ?> objeto(Object valor, String campo) {
        if (valor instanceof Map<?, ?> mapa) {
            return mapa;
        }

        throw new IllegalArgumentException(
                "O campo " + campo + " deve ser um objeto JSON");
    }

    private Boolean booleano(Object valor, String campo) {
        if (valor instanceof Boolean resultado) {
            return resultado;
        }

        throw new IllegalArgumentException(
                "O campo " + campo + " deve ser true ou false");
    }

    private Date data(Object valor, String campo) {
        if (valor == null) {
            return null;
        }

        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " deve ser um timestamp em segundos");
        }

        try {
            long segundos = new BigDecimal(numero.toString()).longValueExact();
            return Date.from(Instant.ofEpochSecond(segundos));
        } catch (ArithmeticException | java.time.DateTimeException e) {
            throw new IllegalArgumentException(
                    "Timestamp inválido no campo " + campo, e);
        }
    }

    private String textoObrigatorio(Object valor, String campo, int tamanhoMaximo) {
        String resultado = texto(valor);

        if (resultado == null) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório");
        }

        if (resultado.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " deve ter no máximo "
                            + tamanhoMaximo + " caracteres");
        }

        return resultado;
    }

    private String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }
        return valor.toString();
    }

    private String cortar(String valor, int maximo) {
        return valor.length() > maximo ? valor.substring(0, maximo) : valor;
    }
}