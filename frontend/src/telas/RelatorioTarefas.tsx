import { useEffect, useState } from "react";
import { requisicao } from "../api";
import { baixarCsv } from "../exportar";

type Tarefa = {
  id: number;
  nome: string;
  prioridade: string | null;
  data_criacao: string | null;
  categoriaTarefa: {
    nome: string;
  } | null;
};

function RelatorioTarefas({ autorizacao }: { autorizacao: string }) {
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [busca, setBusca] = useState("");
  const [de, setDe] = useState("");
  const [ate, setAte] = useState("");
  const [erro, setErro] = useState("");

  useEffect(() => {
    let ativo = true;

    requisicao("/api/tarefas", autorizacao)
      .then((resposta) => {
        if (!resposta.ok) {
          throw new Error(`Erro ao carregar tarefas: HTTP ${resposta.status}`);
        }
        return resposta.json();
      })
      .then((dados: Tarefa[]) => {
        if (ativo) {
          setTarefas(dados);
          setErro("");
        }
      })
      .catch((erro: unknown) => {
        console.error("Não foi possível carregar tarefas:", erro);
        if (ativo) {
          setErro("Não foi possível carregar as tarefas.");
        }
      });

    return () => {
      ativo = false;
    };
  }, [autorizacao]);

  const filtradas = tarefas.filter((tarefa) => {
    const data = tarefa.data_criacao?.slice(0, 10) ?? "";

    return (
      tarefa.nome.toLowerCase().includes(busca.toLowerCase()) &&
      (de === "" || data >= de) &&
      (ate === "" || data <= ate)
    );
  });

  function exportarPlanilha() {
    baixarCsv("tarefas", [
      ["ID", "Nome", "Categoria", "Prioridade", "Criação"],
      ...filtradas.map((tarefa) => [
        tarefa.id,
        tarefa.nome,
        tarefa.categoriaTarefa?.nome,
        tarefa.prioridade,
        tarefa.data_criacao?.slice(0, 10),
      ]),
    ]);
  }

  return (
    <div>
      <div className="filtros">
        <input
          placeholder="Buscar pelo nome"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
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
          <p>{filtradas.length} tarefa(s)</p>

          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Categoria</th>
                <th>Prioridade</th>
                <th>Criação</th>
              </tr>
            </thead>
            <tbody>
              {filtradas.map((tarefa) => (
                <tr key={tarefa.id}>
                  <td>{tarefa.id}</td>
                  <td>{tarefa.nome}</td>
                  <td>{tarefa.categoriaTarefa?.nome ?? "-"}</td>
                  <td>{tarefa.prioridade ?? "-"}</td>
                  <td>{tarefa.data_criacao?.slice(0, 10) ?? "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default RelatorioTarefas;