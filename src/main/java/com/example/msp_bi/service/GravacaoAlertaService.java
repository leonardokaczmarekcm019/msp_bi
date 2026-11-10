package com.example.msp_bi.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.msp_bi.model.Alerta;
import com.example.msp_bi.model.Dispositivo;
import com.example.msp_bi.model.Fabricante;
import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.Modelo;
import com.example.msp_bi.model.ServicoRede;
import com.example.msp_bi.model.TipoAlerta;
import com.example.msp_bi.repository.AlertaRepository;
import com.example.msp_bi.repository.DispositivoRepository;
import com.example.msp_bi.repository.FabricanteRepository;
import com.example.msp_bi.repository.ModeloRepository;
import com.example.msp_bi.repository.ServicoRedeRepository;
import com.example.msp_bi.repository.TipoAlertaRepository;

@Service
public class GravacaoAlertaService {

    private final AlertaRepository alertas;
    private final TipoAlertaRepository tipos;
    private final DispositivoRepository dispositivos;
    private final FabricanteRepository fabricantes;
    private final ModeloRepository modelos;
    private final ServicoRedeRepository servicos;

    public GravacaoAlertaService(
            AlertaRepository alertas,
            TipoAlertaRepository tipos,
            DispositivoRepository dispositivos,
            FabricanteRepository fabricantes,
            ModeloRepository modelos,
            ServicoRedeRepository servicos) {
        this.alertas = alertas;
        this.tipos = tipos;
        this.dispositivos = dispositivos;
        this.fabricantes = fabricantes;
        this.modelos = modelos;
        this.servicos = servicos;
    }

    @Transactional
    public boolean salvarAlerta(FonteDados fonte, Map<String, Object> dados) {
        String id = obrigatorio(dados.get("idExterno"), "alerta.idExterno", 255);
        Object dispositivo = dados.get("dispositivo");
        Object servico = dados.get("servicoRede");
        if ((dispositivo == null) == (servico == null)) {
            throw new IllegalArgumentException("O alerta deve ter um dispositivo ou um serviço de rede");
        }
        Map<?, ?> dadosTipo = objeto(dados.get("tipoAlerta"), "tipoAlerta");
        String nomeTipo = obrigatorio(dadosTipo.get("nome"), "tipoAlerta.nome", 50);
        TipoAlerta tipo = tipos.findByNome(nomeTipo).orElseGet(TipoAlerta::new);
        tipo.setNome(nomeTipo);
        tipo.setDescricao(obrigatorio(dadosTipo.get("descricao"), "tipoAlerta.descricao", 100));
        tipo = tipos.save(tipo);

        Alerta alerta = alertas.findByFonteDadosIdAndIdExterno(fonte.getId(), id)
                .orElseGet(Alerta::new);
        alerta.setFonteDados(fonte);
        alerta.setIdExterno(id);
        alerta.setMensagem(obrigatorio(dados.get("mensagem"), "alerta.mensagem", 250));
        alerta.setPrioridade(obrigatorio(dados.get("prioridade"), "alerta.prioridade", 50));
        alerta.setSeveridade(opcional(dados.get("severidade"), "alerta.severidade", 50));
        alerta.setData(dataObrigatoria(dados.get("data"), "alerta.data"));
        if (!(dados.get("notificacao") instanceof Boolean notificacao)) {
            throw new IllegalArgumentException("alerta.notificacao deve ser true ou false");
        }
        alerta.setNotificacao(notificacao);
        alerta.setTipoAlerta(tipo);
        alerta.setDispositivo(dispositivo == null ? null : salvarDispositivo(objeto(dispositivo, "dispositivo")));
        alerta.setServicoRede(servico == null ? null : salvarServico(objeto(servico, "servicoRede")));
        alertas.save(alerta);
        return true;
    }

    private Dispositivo salvarDispositivo(Map<?, ?> dados) {
        Map<?, ?> dadosFabricante = objeto(dados.get("fabricante"), "dispositivo.fabricante");
        String nomeFabricante = obrigatorio(dadosFabricante.get("nome"), "fabricante.nome", 50);
        Fabricante fabricante = fabricantes.findByNome(nomeFabricante).orElseGet(Fabricante::new);
        fabricante.setNome(nomeFabricante);
        fabricante = fabricantes.save(fabricante);
        Map<?, ?> dadosModelo = objeto(dados.get("modelo"), "dispositivo.modelo");
        String nomeModelo = obrigatorio(dadosModelo.get("nome"), "modelo.nome", 50);
        Modelo modelo = modelos.findByNome(nomeModelo).orElseGet(Modelo::new);
        modelo.setNome(nomeModelo);
        modelo = modelos.save(modelo);
        String nome = obrigatorio(dados.get("nome"), "dispositivo.nome", 50);
        Dispositivo dispositivo = dispositivos.findByNome(nome).orElseGet(Dispositivo::new);
        dispositivo.setNome(nome);
        dispositivo.setEndereco_mac(opcional(dados.get("endereco_mac"), "dispositivo.endereco_mac", 50));
        dispositivo.setEndereco_ip(opcional(dados.get("endereco_ip"), "dispositivo.endereco_ip", 50));
        dispositivo.setUrl(opcional(dados.get("url"), "dispositivo.url", 100));
        dispositivo.setFabricante(fabricante);
        dispositivo.setModelo(modelo);
        return dispositivos.save(dispositivo);
    }

    private ServicoRede salvarServico(Map<?, ?> dados) {
        String nome = obrigatorio(dados.get("nome"), "servicoRede.nome", 50);
        ServicoRede servico = servicos.findByNome(nome).orElseGet(ServicoRede::new);
        servico.setNome(nome);
        servico.setEndereco_ip(obrigatorio(dados.get("endereco_ip"), "servicoRede.endereco_ip", 50));
        servico.setUrl(opcional(dados.get("url"), "servicoRede.url", 100));
        return servicos.save(servico);
    }

    private Map<?, ?> objeto(Object valor, String campo) {
        if (valor instanceof Map<?, ?> mapa) {
            return mapa;
        }
        throw new IllegalArgumentException(campo + " deve ser um objeto");
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
