package com.example.msp_bi.controller;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.CategoriaTarefa;
import com.example.msp_bi.model.ChamadoTecnico;
import com.example.msp_bi.model.FonteDados;
import com.example.msp_bi.model.QuadroTarefa;
import com.example.msp_bi.model.Usuario;
import com.example.msp_bi.repository.AlertaRepository;
import com.example.msp_bi.repository.ChamadoRepository;
import com.example.msp_bi.repository.TarefaRepository;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final ChamadoRepository chamados;
    private final TarefaRepository tarefas;
    private final AlertaRepository alertas;

    public DashboardController(
            ChamadoRepository chamados,
            TarefaRepository tarefas,
            AlertaRepository alertas) {

        this.chamados = chamados;
        this.tarefas = tarefas;
        this.alertas = alertas;
    }

    public record Opcao(Long id, String nome) {
    }

    public record RegistroDashboard(
            Long id,
            String data,
            Boolean ativo,
            Opcao organizacao,
            List<Opcao> tecnicos,
            String categoria,
            String prioridade) {
    }

    @GetMapping("/chamados")
    @Transactional(readOnly = true)
    public List<RegistroDashboard> chamados() {
        return chamados.findAll().stream()
                .map(chamado -> {
                    List<Opcao> tecnicos = chamado.getTecnicos() == null
                            ? List.of()
                            : chamado.getTecnicos().stream()
                                    .map(ChamadoTecnico::getTecnico)
                                    .map(this::tecnico)
                                    .toList();

                    String categoria = chamado.getCategoriaChamado() == null
                            ? null
                            : chamado.getCategoriaChamado().getNome();

                    String prioridade = chamado.getPrioridadeChamado() == null
                            ? null
                            : chamado.getPrioridadeChamado().getNome();

                    return new RegistroDashboard(
                            chamado.getId(),
                            data(chamado.getDataAbertura()),
                            chamado.getDataConclusao() == null,
                            organizacao(chamado.getFonteDados()),
                            tecnicos,
                            categoria,
                            prioridade);
                })
                .toList();
    }

    @GetMapping("/tarefas")
    @Transactional(readOnly = true)
    public List<RegistroDashboard> tarefas() {
        return tarefas.findAll().stream()
                .map(tarefa -> {
                    CategoriaTarefa categoria = tarefa.getCategoriaTarefa();

                    QuadroTarefa quadro = categoria == null
                            ? null
                            : categoria.getQuadroTarefa();

                    FonteDados fonte = quadro == null
                            ? null
                            : quadro.getFonteDados();

                    List<Opcao> tecnicos = tarefa.getTecnicos().stream()
                            .map(this::tecnico)
                            .toList();

                    return new RegistroDashboard(
                            tarefa.getId(),
                            data(tarefa.getData_criacao()),
                            tarefa.getData_conclusao() == null,
                            organizacao(fonte),
                            tecnicos,
                            categoria == null ? null : categoria.getNome(),
                            tarefa.getPrioridade());
                })
                .toList();
    }

    @GetMapping("/alertas")
    @Transactional(readOnly = true)
    public List<RegistroDashboard> alertas() {
        return alertas.findAll().stream()
                .map(alerta -> new RegistroDashboard(
                        alerta.getId(),
                        data(alerta.getData()),
                        null,
                        organizacao(alerta.getFonteDados()),
                        List.of(),
                        alerta.getTipoAlerta() == null
                                ? null
                                : alerta.getTipoAlerta().getNome(),
                        alerta.getPrioridade()))
                .toList();
    }

    private Opcao organizacao(FonteDados fonte) {
        if (fonte == null || fonte.getOrganizacao() == null) {
            return null;
        }

        return new Opcao(
                fonte.getOrganizacao().getId(),
                fonte.getOrganizacao().getNome());
    }

    private Opcao tecnico(Usuario usuario) {
        return new Opcao(
                usuario.getId(),
                usuario.getNome() + " (" + usuario.getEmail() + ")");
    }

    private String data(Date valor) {
        if (valor == null) {
            return null;
        }

        return valor.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .toString();
    }
}