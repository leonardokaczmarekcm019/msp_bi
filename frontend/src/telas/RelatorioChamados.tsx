import { useEffect, useState } from "react";
import { baixarCsv } from "../exportar";
import { requisicao } from "../api";

type Chamado = {
  id: number;
  titulo: string;
  url: string;
  dataAbertura: string | null;
  fonteDados: {
    plataforma: string;
    organizacao: { nome: string } | null;
  } | null;
};

function RelatorioChamados({ autorizacao }: { autorizacao: string }) {
  const [chamados, setChamados] = useState<Chamado[]>([]);
  const [busca, setBusca] = useState("");
  const [organizacao, setOrganizacao] = useState("");
  const [de, setDe] = useState("");
  const [ate, setAte] = useState("");

  useEffect(() => {
  requisicao("/api/chamados", autorizacao)
    .then((resposta) => {
      if (!resposta.ok) {
        throw new Error(`Erro ao carregar chamados: HTTP ${resposta.status}`);
      }
      return resposta.json();
    })
    .then(setChamados)
    .catch((erro: unknown) => {
      console.error("Não foi possível carregar chamados:", erro);
    });
}, [autorizacao]);

  const organizacoes = [
    ...new Set(chamados.map((c) => c.fonteDados?.organizacao?.nome ?? "")),
  ].filter((nome) => nome !== "");

  const filtrados = chamados.filter((c) => {
    const org = c.fonteDados?.organizacao?.nome ?? "";
    const data = c.dataAbertura ? c.dataAbertura.slice(0, 10) : "";

    return (
      c.titulo.toLowerCase().includes(busca.toLowerCase()) &&
      (organizacao === "" || org === organizacao) &&
      (de === "" || data >= de) &&
      (ate === "" || data <= ate)
    );
  });

  function exportarPlanilha() {
    const linhas = filtrados.map((c) => [
      c.id,
      c.titulo,
      c.fonteDados?.organizacao?.nome,
      c.fonteDados?.plataforma,
      c.dataAbertura ? c.dataAbertura.slice(0, 10) : "",
      c.url,
    ]);

    baixarCsv("chamados", [
      ["ID", "Título", "Organização", "Fonte", "Abertura", "URL"],
      ...linhas,
    ]);
  }

  return (
    <div>
      <div className="filtros">
        <input
          placeholder="Buscar pelo título"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />

        <select
          value={organizacao}
          onChange={(e) => setOrganizacao(e.target.value)}
        >
          <option value="">Todas as organizações</option>
          {organizacoes.map((nome) => (
            <option key={nome} value={nome}>
              {nome}
            </option>
          ))}
        </select>

        <input type="date" value={de} onChange={(e) => setDe(e.target.value)} />
        <input type="date" value={ate} onChange={(e) => setAte(e.target.value)} />
      </div>

      <div className="filtros">
        <button onClick={exportarPlanilha}>Exportar planilha</button>
        <button onClick={() => window.print()}>Exportar PDF</button>
      </div>

      <p>{filtrados.length} chamado(s)</p>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Organização</th>
            <th>Fonte</th>
            <th>Abertura</th>
          </tr>
        </thead>
        <tbody>
          {filtrados.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>
                <a href={c.url} target="_blank" rel="noreferrer">
                  {c.titulo}
                </a>
              </td>
              <td>{c.fonteDados?.organizacao?.nome}</td>
              <td>{c.fonteDados?.plataforma}</td>
              <td>{c.dataAbertura ? c.dataAbertura.slice(0, 10) : "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default RelatorioChamados;