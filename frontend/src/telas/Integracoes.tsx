import { useEffect, useState, type FormEvent } from "react";

type FonteDados = {
  id: number;
  plataforma: string;
  tipoFonte: string;
};

type Integracao = {
  id: number;
  tipo: string;
  url: string | null;
  autenticacao: string | null;
  usuario: string | null;
  tokenUrl: string | null;
  escopo: string | null;
  mapeamento: string | null;
  ativo: boolean;
};

function Integracoes() {
  const [fontes, setFontes] = useState<FonteDados[]>([]);
  const [fonteId, setFonteId] = useState("");
  const [integracoes, setIntegracoes] = useState<Integracao[]>([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  const [tipo, setTipo] = useState("API");
  const [url, setUrl] = useState("");
  const [autenticacao, setAutenticacao] = useState("NENHUMA");
  const [usuario, setUsuario] = useState("");
  const [segredo, setSegredo] = useState("");
  const [tokenUrl, setTokenUrl] = useState("");
  const [escopo, setEscopo] = useState("");
  const [mapeamento, setMapeamento] = useState("");
  const [ativo, setAtivo] = useState(true);

  async function carregarFontes() {
    try {
      const resposta = await fetch("/api/fontes-dados");

      if (!resposta.ok) {
        throw new Error();
      }

      setFontes(await resposta.json());
    } catch {
      setErro("Não foi possível carregar as fontes de dados.");
    }
  }

  async function carregar(id: string) {
    if (!id) {
      setIntegracoes([]);
      return;
    }

    setCarregando(true);

    try {
      const resposta = await fetch(`/api/fontes-dados/${id}/integracoes`);

      if (!resposta.ok) {
        throw new Error();
      }

      setIntegracoes(await resposta.json());
    } catch {
      setErro("Não foi possível carregar as integrações.");
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarFontes();
  }, []);

  function escolherFonte(id: string) {
    setErro("");
    setFonteId(id);
    carregar(id);
  }

  function limparFormulario() {
    setEditandoId(null);
    setTipo("API");
    setUrl("");
    setAutenticacao("NENHUMA");
    setUsuario("");
    setSegredo("");
    setTokenUrl("");
    setEscopo("");
    setMapeamento("");
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
      ? `/api/fontes-dados/${fonteId}/integracoes/${editandoId}`
      : `/api/fontes-dados/${fonteId}/integracoes`;

    const resposta = await fetch(endereco, {
      method: editandoId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        tipo,
        url: url || null,
        autenticacao,
        usuario: usuario || null,
        segredo: segredo || null,
        tokenUrl: tokenUrl || null,
        escopo: escopo || null,
        mapeamento: mapeamento || null,
        ativo,
      }),
    });

    if (!resposta.ok) {
      const dados = await resposta.json();
      setErro(dados.errors?.join(", ") || "Erro ao salvar.");
      return;
    }

    limparFormulario();
    carregar(fonteId);
  }

  function editar(integracao: Integracao) {
    setErro("");
    setEditandoId(integracao.id);
    setTipo(integracao.tipo);
    setUrl(integracao.url || "");
    setAutenticacao(integracao.autenticacao || "NENHUMA");
    setUsuario(integracao.usuario || "");
    setSegredo("");
    setTokenUrl(integracao.tokenUrl || "");
    setEscopo(integracao.escopo || "");
    setMapeamento(integracao.mapeamento || "");
    setAtivo(integracao.ativo);
    setModalAberto(true);
  }

  async function excluir(id: number) {
    if (!window.confirm("Deseja excluir esta integração?")) {
      return;
    }

    setErro("");

    const resposta = await fetch(
      `/api/fontes-dados/${fonteId}/integracoes/${id}`,
      { method: "DELETE" }
    );

    if (!resposta.ok) {
      setErro("Não foi possível excluir a integração.");
      return;
    }

    carregar(fonteId);
  }

  return (
    <div>
      <h2>Integrações</h2>

      <select value={fonteId} onChange={(e) => escolherFonte(e.target.value)}>
        <option value="">Selecione uma fonte de dados</option>
        {fontes.map((fonte) => (
          <option key={fonte.id} value={fonte.id}>
            {fonte.plataforma} ({fonte.tipoFonte})
          </option>
        ))}
      </select>

      {fonteId && <button onClick={novo}>Nova integração</button>}

      {modalAberto && (
        <div className="modal-fundo">
          <form className="modal" onSubmit={salvar}>
            <h3>{editandoId ? "Editar integração" : "Nova integração"}</h3>

            <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
              <option value="API">API</option>
              <option value="WEBHOOK">Webhook</option>
            </select>
            <input
              placeholder="URL"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
            />
            <select
              value={autenticacao}
              onChange={(e) => setAutenticacao(e.target.value)}
            >
              <option value="NENHUMA">Nenhuma</option>
              <option value="BASIC">Basic</option>
              <option value="BEARER">Bearer</option>
              <option value="OAUTH2">OAuth2</option>
            </select>
            <input
              placeholder="Usuário"
              value={usuario}
              onChange={(e) => setUsuario(e.target.value)}
            />
            <input
              type="password"
              placeholder={
                editandoId ? "Segredo (vazio mantém o atual)" : "Segredo"
              }
              value={segredo}
              onChange={(e) => setSegredo(e.target.value)}
            />
            <input
              placeholder="URL do token"
              value={tokenUrl}
              onChange={(e) => setTokenUrl(e.target.value)}
            />
            <input
              placeholder="Escopo"
              value={escopo}
              onChange={(e) => setEscopo(e.target.value)}
            />
            <textarea
              placeholder="Mapeamento"
              value={mapeamento}
              onChange={(e) => setMapeamento(e.target.value)}
            />
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

      {fonteId && !carregando && integracoes.length === 0 && (
        <p>Nenhuma integração cadastrada para esta fonte.</p>
      )}

      {integracoes.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Tipo</th>
              <th>URL</th>
              <th>Autenticação</th>
              <th>Usuário</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {integracoes.map((integracao) => (
              <tr key={integracao.id}>
                <td>{integracao.tipo}</td>
                <td>{integracao.url || "—"}</td>
                <td>{integracao.autenticacao || "—"}</td>
                <td>{integracao.usuario || "—"}</td>
                <td>{integracao.ativo ? "Ativa" : "Inativa"}</td>
                <td>
                  <button onClick={() => editar(integracao)}>Editar</button>
                  <button onClick={() => excluir(integracao.id)}>
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Integracoes;
