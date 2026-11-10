import { useCallback, useEffect, useRef, useState } from "react";

import { mensagemErro, mensagemExcecao, requisicao } from "../api";
import PainelDashboard from "./PainelDashboard";

type DashboardProps = {
  autorizacao: string;
};

function Dashboard({ autorizacao }: DashboardProps) {
  const [sincronizando, setSincronizando] = useState(false);
  const [resultado, setResultado] = useState("");
  const [erro, setErro] = useState("");
  const [versao, setVersao] = useState(0);
  const [erroAgendamento, setErroAgendamento] = useState("");
  const [coletaEmAndamento, setColetaEmAndamento] = useState(false);
  const [proximaColeta, setProximaColeta] = useState<number | null>(null);
  const [agora, setAgora] = useState(() => Date.now());
  const requisicaoAtual = useRef(0);
  const coletasConcluidas = useRef<number | null>(null);

  const atualizarAgendamento = useCallback(() => {
    const numero = ++requisicaoAtual.current;
    requisicao("/api/coleta/estado", autorizacao, { cache: "no-store" })
      .then((resposta) => {
        if (!resposta.ok) {
          throw new Error(`Erro ao consultar o agendamento (HTTP ${resposta.status}).`);
        }
        return resposta.json();
      })
      .then((estado: unknown) => {
        if (typeof estado !== "object" || estado === null
            || !("sincronizando" in estado) || typeof estado.sincronizando !== "boolean"
            || !("proximaEmMs" in estado)
            || !(estado.proximaEmMs === null || (typeof estado.proximaEmMs === "number"
              && Number.isFinite(estado.proximaEmMs) && estado.proximaEmMs >= 0))
            || !("coletasConcluidas" in estado) || typeof estado.coletasConcluidas !== "number") {
          throw new Error("O servidor retornou um agendamento inválido.");
        }
        if (numero !== requisicaoAtual.current) return;

        const instante = Date.now();
        setAgora(instante);
        setProximaColeta(estado.proximaEmMs === null ? null : instante + estado.proximaEmMs);
        setColetaEmAndamento(estado.sincronizando);
        setErroAgendamento("");
        if (coletasConcluidas.current !== null
            && coletasConcluidas.current !== estado.coletasConcluidas) {
          setVersao((atual) => atual + 1);
        }
        coletasConcluidas.current = estado.coletasConcluidas;
      })
      .catch((erro: unknown) => {
        if (numero === requisicaoAtual.current) {
          setProximaColeta(null);
          setErroAgendamento(mensagemExcecao(erro));
        }
      });
  }, [autorizacao]);

  useEffect(() => {
    const atual = requisicaoAtual;
    atualizarAgendamento();
    const consulta = setInterval(atualizarAgendamento, 5000);
    const relogio = setInterval(() => setAgora(Date.now()), 1000);
    return () => {
      atual.current++;
      clearInterval(consulta);
      clearInterval(relogio);
    };
  }, [atualizarAgendamento]);

  const segundos = proximaColeta === null ? null
    : Math.max(0, Math.ceil((proximaColeta - agora) / 1000));
  const tempo = segundos === null ? null
    : `${Math.floor(segundos / 60)}:${String(segundos % 60).padStart(2, "0")}`;

  async function sincronizar() {
    setSincronizando(true);
    setResultado("");
    setErro("");

    try {
      const resposta = await requisicao("/api/coleta/executar", autorizacao, {
        method: "POST",
      });

      if (!resposta.ok) {
        setErro(await mensagemErro(resposta, "Erro ao executar a coleta"));

        // Parte da coleta pode ter sido salva antes de uma falha.
        setVersao((atual) => atual + 1);
        return;
      }

      const texto = await resposta.text();
      setResultado(texto);
      setVersao((atual) => atual + 1);
    } catch (erro) {
      setErro(mensagemExcecao(erro));

      // Atualiza os painéis caso a coleta tenha sido parcialmente gravada.
      setVersao((atual) => atual + 1);
    } finally {
      setSincronizando(false);
      atualizarAgendamento();
    }
  }

  return (
    <div>
      <h1>Dashboard</h1>

      <button
        type="button"
        onClick={sincronizar}
        disabled={sincronizando || coletaEmAndamento}
      >
        {sincronizando || coletaEmAndamento ? "Sincronizando..." : "Sincronizar agora"}
      </button>

      {erroAgendamento ? (
        <p className="erro" role="alert">Não foi possível consultar a próxima sincronização: {erroAgendamento}</p>
      ) : sincronizando || coletaEmAndamento ? (
        <p>Sincronização em andamento. O contador reinicia ao terminar.</p>
      ) : tempo !== null ? (
        <p>Próxima sincronização automática em: {tempo}</p>
      ) : (
        <p>Consultando próxima sincronização...</p>
      )}

      {resultado && <p role="status">{resultado}</p>}
      {erro && <p className="erro" role="alert">{erro}</p>}

      <div className="paineis-dashboard">
        <PainelDashboard
          tipo="chamados"
          titulo="Chamados"
          autorizacao={autorizacao}
          versao={versao}
        />

        <PainelDashboard
          tipo="tarefas"
          titulo="Tarefas"
          autorizacao={autorizacao}
          versao={versao}
        />

        <PainelDashboard
          tipo="alertas"
          titulo="Alertas"
          autorizacao={autorizacao}
          versao={versao}
        />
      </div>
    </div>
  );
}

export default Dashboard;