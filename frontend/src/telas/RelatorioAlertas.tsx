import { useEffect, useState } from "react";
import { requisicao } from "../api";
import { baixarCsv } from "../exportar";

type Alerta = {
  id: number;
  mensagem: string;
  prioridade: string;
  severidade: string | null;
  data: string | null;
  tipoAlerta: { nome: string } | null;
  dispositivo: { nome: string } | null;
  servicoRede: { nome: string } | null;
  fonteDados: {
    plataforma: string;
    organizacao: { nome: string } | null;
  } | null;
};

function RelatorioAlertas({ autorizacao }: { autorizacao: string }) {
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  const [busca, setBusca] = useState("");
  const [organizacao, setOrganizacao] = useState("");
  const [de, setDe] = useState("");
  const [ate, setAte] = useState("");
  const [erro, setErro] = useState("");

  useEffect(() => {
    let ativo = true;

    requisicao("/api/alertas", autorizacao)
      .then((resposta) => {
        if (!resposta.ok) {
          throw new Error(`Erro ao carregar alertas: HTTP ${resposta.status}`);
        }
        return resposta.json();
      })
      .then((dados: Alerta[]) => {
        if (ativo) {
          setAlertas(dados);
          setErro("");
        }
      })
      .catch((erro: unknown) => {
        console.error("Não foi possível carregar alertas:", erro);
        if (ativo) {
          setErro("Não foi possível carregar os alertas.");
        }
      });

    return () => {
      ativo = false;
    };
  }, [autorizacao]);

  const organizacoes = [
    ...new Set(alertas.map((a) => a.fonteDados?.organizacao?.nome ?? "")),
  ].filter((nome) => nome !== "");

  const filtrados = alertas.filter((alerta) => {
    const org = alerta.fonteDados?.organizacao?.nome ?? "";
    const data = alerta.data?.slice(0, 10) ?? "";

    return (
      alerta.mensagem.toLowerCase().includes(busca.toLowerCase()) &&
      (organizacao === "" || org === organizacao) &&
      (de === "" || data >= de) &&
      (ate === "" || data <= ate)
    );
  });

  function exportarPlanilha() {
    baixarCsv("alertas", [
      ["ID", "Mensagem", "Tipo", "Recurso", "Prioridade", "Severidade", "Organização", "Fonte", "Data"],
      ...filtrados.map((alerta) => [
        alerta.id,
        alerta.mensagem,
        alerta.tipoAlerta?.nome,
        alerta.dispositivo?.nome ?? alerta.servicoRede?.nome,
        alerta.prioridade,
        alerta.severidade,
        alerta.fonteDados?.organizacao?.nome,
        alerta.fonteDados?.plataforma,
        alerta.data?.slice(0, 10),
      ]),
    ]);
  }

  return (
    <div>
      <div className="filtros">
        <input
          placeholder="Buscar pela mensagem"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />

        <select
          value={organizacao}
          onChange={(e) => setOrganizacao(e.target.value)}
        >
          <option value="">Todas as organizações</option>
          {organizacoes.map((nome) => (
            <option key={nome} value={nome}>{nome}</option>
          ))}
        </select>

        <input type="date" value={de} onChange={(e) => setDe(e.target.value)} />
        <input type="date" value={ate} onChange={(e) => setAte(e.target.value)} />
      </div>

      <div className="filtros">
        <button onClick={exportarPlanilha} disabled={!!erro}>
          Exportar planilha
        </button>
        <button onClick={() => window.print()}>Exportar PDF</button>
      </div>

      {erro ? (
        <p role="alert">{erro}</p>
      ) : (
        <>
          <p>{filtrados.length} alerta(s)</p>

          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Mensagem</th>
                <th>Tipo</th>
                <th>Dispositivo ou serviço</th>
                <th>Prioridade</th>
                <th>Severidade</th>
                <th>Organização</th>
                <th>Fonte</th>
                <th>Data</th>
              </tr>
            </thead>
            <tbody>
              {filtrados.map((alerta) => (
                <tr key={alerta.id}>
                  <td>{alerta.id}</td>
                  <td>{alerta.mensagem}</td>
                  <td>{alerta.tipoAlerta?.nome ?? "-"}</td>
                  <td>
                    {alerta.dispositivo?.nome ?? alerta.servicoRede?.nome ?? "-"}
                  </td>
                  <td>{alerta.prioridade}</td>
                  <td>{alerta.severidade ?? "-"}</td>
                  <td>{alerta.fonteDados?.organizacao?.nome ?? "-"}</td>
                  <td>{alerta.fonteDados?.plataforma ?? "-"}</td>
                  <td>{alerta.data?.slice(0, 10) ?? "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default RelatorioAlertas;