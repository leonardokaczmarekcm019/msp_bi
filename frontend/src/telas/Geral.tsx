import { useEffect, useState, type FormEvent } from "react";
import { mensagemErro, mensagemExcecao, requisicao } from "../api";

function Geral({ autorizacao }: { autorizacao: string }) {
  const [minutos, setMinutos] = useState("");
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    let ativo = true;
    requisicao("/api/coleta/configuracao", autorizacao, { cache: "no-store" })
      .then((resposta) => {
        if (!resposta.ok) throw new Error(`Erro ao carregar configuração (HTTP ${resposta.status}).`);
        return resposta.json();
      })
      .then((valor: unknown) => {
        if (typeof valor !== "number" || !Number.isFinite(valor) || valor <= 0) {
          throw new Error("O servidor retornou um intervalo inválido.");
        }
        if (ativo) setMinutos(String(valor));
      })
      .catch((erro: unknown) => {
        if (ativo) setErro(mensagemExcecao(erro));
      });
    return () => { ativo = false; };
  }, [autorizacao]);

  async function salvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");
    setMensagem("");
    setSalvando(true);
    try {
      const resposta = await requisicao("/api/coleta/configuracao", autorizacao, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ intervaloMinutos: Number(minutos) }),
      });
      if (!resposta.ok) {
        setErro(await mensagemErro(resposta, "Não foi possível salvar o intervalo"));
        return;
      }
      setMensagem("Intervalo salvo. A próxima coleta usará o novo tempo.");
    } catch (erro) {
      setErro(mensagemExcecao(erro));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div>
      <h2>Geral</h2>
      <form onSubmit={salvar}>
        <label>
          Intervalo de sincronização (minutos)
          <input
            type="number"
            min="1"
            max="2147483647"
            step="1"
            value={minutos}
            onChange={(evento) => setMinutos(evento.target.value)}
            required
          />
        </label>
        <button type="submit" disabled={salvando}>
          {salvando ? "Salvando..." : "Salvar"}
        </button>
      </form>
      {mensagem && <p role="status">{mensagem}</p>}
      {erro && <p className="erro" role="alert">{erro}</p>}
    </div>
  );
}

export default Geral;
