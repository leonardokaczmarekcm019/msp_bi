import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import { mensagemExcecao, requisicao } from "../api";

type TipoPainel = "chamados" | "tarefas" | "alertas";
type TipoGrafico = "barras" | "linha" | "pizza";
type Agrupamento = "categoria" | "prioridade" | "organizacao" | "data";
type Periodo = "todos" | "7" | "30";
type Situacao = "todos" | "ativos" | "concluidos";

type Opcao = {
  id: number;
  nome: string;
};

type Registro = {
  id: number;
  data: string | null;
  ativo: boolean | null;
  organizacao: Opcao | null;
  tecnicos: Opcao[];
  categoria: string | null;
  prioridade: string | null;
};

type Configuracao = {
  grafico: TipoGrafico;
  agrupamento: Agrupamento;
  periodo: Periodo;
  situacao: Situacao;
  organizacao: string;
  tecnico: string;
};

type EstadoConfiguracao = {
  configuracao: Configuracao;
  aviso: string;
};

type Props = {
  tipo: TipoPainel;
  titulo: string;
  autorizacao: string;
  versao: number;
};

const configuracaoInicial: Configuracao = {
  grafico: "barras",
  agrupamento: "categoria",
  periodo: "todos",
  situacao: "todos",
  organizacao: "",
  tecnico: "",
};

const cores = [
  "#4f46e5",
  "#0891b2",
  "#16a34a",
  "#ea580c",
  "#db2777",
  "#9333ea",
  "#ca8a04",
];

function chaveConfiguracao(tipo: TipoPainel) {
  return `dashboard-${tipo}`;
}

function configuracaoValida(valor: unknown): valor is Configuracao {
  if (typeof valor !== "object" || valor === null) {
    return false;
  }

  const item = valor as Record<string, unknown>;

  return (
    (item.grafico === "barras" ||
      item.grafico === "linha" ||
      item.grafico === "pizza") &&
    (item.agrupamento === "categoria" ||
      item.agrupamento === "prioridade" ||
      item.agrupamento === "organizacao" ||
      item.agrupamento === "data") &&
    (item.periodo === "todos" || item.periodo === "7" || item.periodo === "30") &&
    (item.situacao === "todos" ||
      item.situacao === "ativos" ||
      item.situacao === "concluidos") &&
    typeof item.organizacao === "string" &&
    typeof item.tecnico === "string"
  );
}

function carregarConfiguracao(tipo: TipoPainel): EstadoConfiguracao {
  try {
    const texto = localStorage.getItem(chaveConfiguracao(tipo));

    if (texto === null) {
      return { configuracao: configuracaoInicial, aviso: "" };
    }

    const valor: unknown = JSON.parse(texto);

    if (!configuracaoValida(valor)) {
      return {
        configuracao: configuracaoInicial,
        aviso: "Configuração salva inválida. Foram usados os valores iniciais.",
      };
    }

    return { configuracao: valor, aviso: "" };
  } catch (erro) {
    console.error("Erro ao carregar configuração do dashboard:", erro);

    return {
      configuracao: configuracaoInicial,
      aviso: "Não foi possível carregar a configuração salva neste navegador.",
    };
  }
}

function listarOpcoes(opcoes: (Opcao | null)[]): Opcao[] {
  const porId = new Map<number, Opcao>();

  for (const opcao of opcoes) {
    if (opcao !== null) {
      porId.set(opcao.id, opcao);
    }
  }

  return [...porId.values()].sort((a, b) => a.nome.localeCompare(b.nome));
}

function primeiroDia(periodo: Periodo): string | null {
  if (periodo === "todos") {
    return null;
  }

  const hoje = new Date();
  const dias = Number(periodo);

  const inicio = new Date(
    Date.UTC(
      hoje.getUTCFullYear(),
      hoje.getUTCMonth(),
      hoje.getUTCDate() - (dias - 1),
    ),
  );

  return inicio.toISOString().slice(0, 10);
}

function hojeUtc(): string {
  return new Date().toISOString().slice(0, 10);
}

function PainelDashboard({ tipo, titulo, autorizacao, versao }: Props) {
  const [registros, setRegistros] = useState<Registro[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [coresOrganizacao, setCoresOrganizacao] = useState<Record<number, string>>({});

  useEffect(() => {
    requisicao("/api/cores-organizacao", autorizacao)
      .then((resposta) => (resposta.ok ? resposta.json() : {}))
      .then(setCoresOrganizacao)
      .catch(() => setCoresOrganizacao({}));
  }, [autorizacao, versao]);

  const [estadoConfig, setEstadoConfig] = useState<EstadoConfiguracao>(
    () => carregarConfiguracao(tipo),
  );

  const config = estadoConfig.configuracao;

  function atualizarConfig(alteracao: Partial<Configuracao>) {
    const nova = { ...config, ...alteracao };

    try {
      localStorage.setItem(chaveConfiguracao(tipo), JSON.stringify(nova));
      setEstadoConfig({ configuracao: nova, aviso: "" });
    } catch (erro) {
      console.error("Erro ao salvar configuração do dashboard:", erro);

      setEstadoConfig({
        configuracao: nova,
        aviso: "O filtro foi aplicado, mas não pôde ser salvo neste navegador.",
      });
    }
  }

  useEffect(() => {
    let ativo = true;

    async function carregar() {
      setCarregando(true);
      setErro("");

      try {
        const resposta = await requisicao(
          `/api/dashboard/${tipo}`,
          autorizacao,
        );

        if (!resposta.ok) {
          throw new Error(
            `Não foi possível carregar ${titulo}: HTTP ${resposta.status}.`,
          );
        }

        const dados: Registro[] = await resposta.json();

        if (ativo) {
          setRegistros(dados);
        }
      } catch (erro) {
        console.error(`Erro ao carregar ${titulo}:`, erro);

        if (ativo) {
          setErro(mensagemExcecao(erro));
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }

    carregar();

    return () => {
      ativo = false;
    };
  }, [tipo, titulo, autorizacao, versao]);

  const organizacoes = listarOpcoes(
    registros.map((registro) => registro.organizacao),
  );

  const tecnicos = listarOpcoes(
    registros.flatMap((registro) => registro.tecnicos),
  );

  const inicio = primeiroDia(config.periodo);
  const hoje = hojeUtc();

  const filtrados = registros.filter((registro) => {
    if (inicio !== null) {
      if (registro.data === null || registro.data < inicio || registro.data > hoje) {
        return false;
      }
    }

    if (
      config.organizacao !== "" &&
      String(registro.organizacao?.id) !== config.organizacao
    ) {
      return false;
    }

    if (
      tipo !== "alertas" &&
      config.tecnico !== "" &&
      !registro.tecnicos.some(
        (tecnico) => String(tecnico.id) === config.tecnico,
      )
    ) {
      return false;
    }

    if (tipo !== "alertas") {
      if (config.situacao === "ativos" && registro.ativo !== true) {
        return false;
      }

      if (config.situacao === "concluidos" && registro.ativo !== false) {
        return false;
      }
    }

    return true;
  });

  const contagem = new Map<string, number>();
  const corPorNome = new Map<string, string>();
  const corFiltrada =
    config.organizacao !== "" ? coresOrganizacao[Number(config.organizacao)] : undefined;

  for (const registro of filtrados) {
    let nome: string;

    switch (config.agrupamento) {
      case "categoria":
        nome = registro.categoria ?? "Não informado";
        break;

      case "prioridade":
        nome = registro.prioridade ?? "Não informada";
        break;

      case "organizacao":
        nome = registro.organizacao
          ? `${registro.organizacao.nome} (#${registro.organizacao.id})`
          : "Não informada";
        if (registro.organizacao && coresOrganizacao[registro.organizacao.id]) {
          corPorNome.set(nome, coresOrganizacao[registro.organizacao.id]);
        }
        break;

      case "data":
        nome = registro.data ?? "Sem data";
        break;
    }

    contagem.set(nome, (contagem.get(nome) ?? 0) + 1);
  }

  const dadosGrafico = [...contagem.entries()]
    .map(([nome, quantidade]) => ({ nome, quantidade }))
    .sort((a, b) => a.nome.localeCompare(b.nome));

  const organizacaoSalvaExiste = organizacoes.some(
    (item) => String(item.id) === config.organizacao,
  );

  const tecnicoSalvoExiste = tecnicos.some(
    (item) => String(item.id) === config.tecnico,
  );

  return (
    <section className="painel-dashboard">
      <h2>{titulo}</h2>

      <div className="filtros-dashboard">
        <label>
          Gráfico
          <select
            value={config.grafico}
            onChange={(e) =>
              atualizarConfig({ grafico: e.target.value as TipoGrafico })
            }
          >
            <option value="barras">Barras</option>
            <option value="linha">Linha</option>
            <option value="pizza">Pizza</option>
          </select>
        </label>

        <label>
          Agrupar por
          <select
            value={config.agrupamento}
            onChange={(e) =>
              atualizarConfig({
                agrupamento: e.target.value as Agrupamento,
              })
            }
          >
            <option value="categoria">
              {tipo === "alertas" ? "Tipo de alerta" : "Categoria"}
            </option>
            <option value="prioridade">Prioridade</option>
            <option value="organizacao">Organização</option>
            <option value="data">Data</option>
          </select>
        </label>

        <label>
          Período
          <select
            value={config.periodo}
            onChange={(e) =>
              atualizarConfig({ periodo: e.target.value as Periodo })
            }
          >
            <option value="todos">Todo o período</option>
            <option value="7">Últimos 7 dias</option>
            <option value="30">Últimos 30 dias</option>
          </select>
        </label>

        <label>
          Organização
          <select
            value={config.organizacao}
            onChange={(e) => atualizarConfig({ organizacao: e.target.value })}
          >
            <option value="">Todas</option>

            {config.organizacao !== "" && !organizacaoSalvaExiste && (
              <option value={config.organizacao}>
                Organização salva não encontrada
              </option>
            )}

            {organizacoes.map((organizacao) => (
              <option key={organizacao.id} value={String(organizacao.id)}>
                {organizacao.nome} (#{organizacao.id})
              </option>
            ))}
          </select>
        </label>

        {tipo !== "alertas" && (
          <>
            <label>
              Situação
              <select
                value={config.situacao}
                onChange={(e) =>
                  atualizarConfig({ situacao: e.target.value as Situacao })
                }
              >
                <option value="todos">Todas</option>
                <option value="ativos">Ativos</option>
                <option value="concluidos">Concluídos</option>
              </select>
            </label>

            <label>
              Técnico / responsável
              <select
                value={config.tecnico}
                onChange={(e) => atualizarConfig({ tecnico: e.target.value })}
              >
                <option value="">Todos</option>

                {config.tecnico !== "" && !tecnicoSalvoExiste && (
                  <option value={config.tecnico}>
                    Técnico salvo não encontrado
                  </option>
                )}

                {tecnicos.map((tecnico) => (
                  <option key={tecnico.id} value={String(tecnico.id)}>
                    {tecnico.nome}
                  </option>
                ))}
              </select>
            </label>
          </>
        )}
      </div>

      {estadoConfig.aviso && <p role="status">{estadoConfig.aviso}</p>}
      {carregando && <p>Carregando dados...</p>}
      {erro && <p className="erro" role="alert">{erro}</p>}

      {!carregando && !erro && (
        <>
          <p>{filtrados.length} registro(s) encontrado(s)</p>

          {dadosGrafico.length === 0 ? (
            <p>Nenhum registro para os filtros selecionados.</p>
          ) : (
            <div className="grafico-dashboard">
              <ResponsiveContainer width="100%" height="100%">
                {config.grafico === "pizza" ? (
                  <PieChart>
                    <Pie
                      data={dadosGrafico}
                      dataKey="quantidade"
                      nameKey="nome"
                      outerRadius="70%"
                    >
                      {dadosGrafico.map((item, indice) => (
                        <Cell
                          key={item.nome}
                          fill={corPorNome.get(item.nome) ?? cores[indice % cores.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                ) : config.grafico === "linha" ? (
                  <LineChart data={dadosGrafico}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="nome" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="quantidade"
                      name="Quantidade"
                      stroke={corFiltrada ?? "#4f46e5"}
                    />
                  </LineChart>
                ) : (
                  <BarChart data={dadosGrafico}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="nome" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Legend />
                    <Bar
                      dataKey="quantidade"
                      name="Quantidade"
                      fill={corFiltrada ?? "#4f46e5"}
                    >
                      {dadosGrafico.map((item) => (
                        <Cell
                          key={item.nome}
                          fill={corPorNome.get(item.nome) ?? corFiltrada ?? "#4f46e5"}
                        />
                      ))}
                    </Bar>
                  </BarChart>
                )}
              </ResponsiveContainer>
            </div>
          )}
        </>
      )}
    </section>
  );
}

export default PainelDashboard;