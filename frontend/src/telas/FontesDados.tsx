import { useEffect, useState, type FormEvent } from "react";

type Organizacao = {
  id: number;
  nome: string;
};

type FonteDados = {
  id: number;
  plataforma: string;
  tipoFonte: string;
  url: string | null;
  ativo: boolean;
  organizacao: Organizacao | null;
};

function FontesDados() {
  const [fontes, setFontes] = useState<FonteDados[]>([]);
  const [organizacoes, setOrganizacoes] = useState<Organizacao[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  const [plataforma, setPlataforma] = useState("");
  const [tipoFonte, setTipoFonte] = useState("CHAMADO");
  const [url, setUrl] = useState("");
  const [organizacaoId, setOrganizacaoId] = useState("");
  const [ativo, setAtivo] = useState(true);

  async function carregar() {
    try {
      const resposta = await fetch("/api/fontes-dados");

      if (!resposta.ok) {
        throw new Error();
      }

      setFontes(await resposta.json());
    } catch {
      setErro("Não foi possível carregar as fontes de dados.");
    } finally {
      setCarregando(false);
    }
  }

  async function carregarOrganizacoes() {
    try {
      const resposta = await fetch("/api/organizacoes");

      if (!resposta.ok) {
        throw new Error();
      }

      setOrganizacoes(await resposta.json());
    } catch {
      setErro("Não foi possível carregar as organizações.");
    }
  }

  useEffect(() => {
    carregar();
    carregarOrganizacoes();
  }, []);

  function limparFormulario() {
    setEditandoId(null);
    setPlataforma("");
    setTipoFonte("CHAMADO");
    setUrl("");
    setOrganizacaoId("");
    setAtivo(true);
    setModalAberto(false);
  }

  function novo() {
    limparFormulario();
    setErro("");
    setModalAberto(true);
  }

  async function salvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    const endereco = editandoId
      ? `/api/fontes-dados/${editandoId}`
      : "/api/fontes-dados";

    const resposta = await fetch(endereco, {
      method: editandoId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        plataforma,
        tipoFonte,
        url: url || null,
        organizacao: organizacaoId ? { id: Number(organizacaoId) } : null,
        ativo,
      }),
    });

    if (!resposta.ok) {
      const dados = await resposta.json();
      setErro(dados.errors?.join(", ") || "Erro ao salvar.");
      return;
    }

    limparFormulario();
    carregar();
  }

  function editar(fonte: FonteDados) {
    setErro("");
    setEditandoId(fonte.id);
    setPlataforma(fonte.plataforma);
    setTipoFonte(fonte.tipoFonte);
    setUrl(fonte.url || "");
    setOrganizacaoId(fonte.organizacao ? String(fonte.organizacao.id) : "");
    setAtivo(fonte.ativo);
    setModalAberto(true);
  }

  async function excluir(id: number) {
    if (!window.confirm("Deseja excluir esta fonte de dados?")) {
      return;
    }

    setErro("");

    const resposta = await fetch(`/api/fontes-dados/${id}`, {
      method: "DELETE",
    });

    if (!resposta.ok) {
      setErro("Não foi possível excluir.");
      return;
    }

    carregar();
  }

  return (
    <div>
      <h2>Fontes de Dados</h2>

      <button onClick={novo}>Nova fonte de dados</button>

      {modalAberto && (
        <div className="modal-fundo">
          <form className="modal" onSubmit={salvar}>
            <h3>{editandoId ? "Editar fonte de dados" : "Nova fonte de dados"}</h3>

            <input
              placeholder="Plataforma"
              value={plataforma}
              onChange={(e) => setPlataforma(e.target.value)}
              required
            />
            <select
              value={tipoFonte}
              onChange={(e) => setTipoFonte(e.target.value)}
            >
              <option value="CHAMADO">Chamado</option>
              <option value="ALERTA">Alerta</option>
              <option value="TAREFA">Tarefa</option>
            </select>
            <input
              placeholder="URL"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
            />
            <select
              value={organizacaoId}
              onChange={(e) => setOrganizacaoId(e.target.value)}
            >
              <option value="">Sem organização</option>
              {organizacoes.map((organizacao) => (
                <option key={organizacao.id} value={organizacao.id}>
                  {organizacao.nome}
                </option>
              ))}
            </select>
            <label>
              <input
                type="checkbox"
                checked={ativo}
                onChange={(e) => setAtivo(e.target.checked)}
              />
              Ativa
            </label>

            {erro && <p className="erro">{erro}</p>}

            <div>
              <button type="submit">Salvar</button>
              <button type="button" onClick={limparFormulario}>
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {!modalAberto && erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && fontes.length === 0 && (
        <p>Nenhuma fonte de dados cadastrada.</p>
      )}

      {fontes.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Plataforma</th>
              <th>Tipo</th>
              <th>URL</th>
              <th>Organização</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {fontes.map((fonte) => (
              <tr key={fonte.id}>
                <td>{fonte.plataforma}</td>
                <td>{fonte.tipoFonte}</td>
                <td>{fonte.url || "—"}</td>
                <td>{fonte.organizacao?.nome || "—"}</td>
                <td>{fonte.ativo ? "Ativa" : "Inativa"}</td>
                <td>
                  <button onClick={() => editar(fonte)}>Editar</button>
                  <button onClick={() => excluir(fonte.id)}>Excluir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default FontesDados;
