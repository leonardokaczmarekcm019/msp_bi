import { useCallback, useEffect, useState, type FormEvent } from "react";
import { mensagemErro, mensagemExcecao, requisicao } from "../api";

type TipoIntegracao = "API" | "WEBHOOK";

type FonteDados = {
  id: number;
  plataforma: string;
  tipoFonte: string;
};

type Integracao = {
  id: number;
  tipo: TipoIntegracao;
  url: string | null;
  autenticacao: string | null;
  usuario: string | null;
  webhookToken: string | null;
  mapeamento: string | null;
  ativo: boolean;
};

function Integracoes({ autorizacao }: { autorizacao: string }) {
  const [fontes, setFontes] = useState<FonteDados[]>([]);
  const [fonteId, setFonteId] = useState("");
  const [integracoes, setIntegracoes] = useState<Integracao[]>([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  const [tipo, setTipo] = useState<TipoIntegracao>("API");
  const [url, setUrl] = useState("");
  const [usuario, setUsuario] = useState("");
  const [segredo, setSegredo] = useState("");
  const [mapeamento, setMapeamento] = useState("");
  const [ativo, setAtivo] = useState(true);

  const carregarFontes = useCallback(() => {
    requisicao("/api/fontes-dados", autorizacao)
      .then((resposta) => {
        if (!resposta.ok) throw new Error();
        return resposta.json();
      })
      .then(setFontes)
      .catch(() => setErro("Não foi possível carregar as fontes de dados."));
  }, [autorizacao]);

  async function carregar(id: string) {
    if (!id) {
      setIntegracoes([]);
      return;
    }

    setCarregando(true);

    try {
      const resposta = await requisicao(
        `/api/fontes-dados/${id}/integracoes`,
        autorizacao,
      );

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
  }, [carregarFontes]);

  function escolherFonte(id: string) {
    setErro("");
    setFonteId(id);
    carregar(id);
  }

  function alterarTipo(valor: string) {
    if (valor === "API" || valor === "WEBHOOK") {
      setTipo(valor);
    }
  }

  function limparFormulario() {
    setEditandoId(null);
    setTipo("API");
    setUrl("");
    setUsuario("");
    setSegredo("");
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

    try {
      const resposta = await requisicao(endereco, autorizacao, {
        method: editandoId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          tipo,
          url: tipo === "API" ? url || null : null,
          autenticacao: tipo === "API" ? "BASIC" : null,
          usuario: tipo === "API" ? usuario || null : null,
          segredo: tipo === "API" ? segredo || null : null,
          mapeamento: tipo === "API" ? mapeamento || null : null,
          ativo,
        }),
      });

      if (!resposta.ok) {
        setErro(await mensagemErro(resposta, "Erro ao salvar"));
        return;
      }

      limparFormulario();
      carregar(fonteId);
    } catch (erro) {
      setErro(mensagemExcecao(erro));
    }
  }

  function editar(integracao: Integracao) {
    setErro("");
    setEditandoId(integracao.id);
    setTipo(integracao.tipo);
    setUrl(integracao.url || "");
    setUsuario(integracao.usuario || "");
    setSegredo("");
    setMapeamento(integracao.mapeamento || "");
    setAtivo(integracao.ativo);
    setModalAberto(true);
  }

  async function excluir(id: number) {
    if (!window.confirm("Deseja excluir esta integração?")) {
      return;
    }

    setErro("");

    try {
      const resposta = await requisicao(
        `/api/fontes-dados/${fonteId}/integracoes/${id}`,
        autorizacao,
        { method: "DELETE" },
      );

      if (!resposta.ok) {
        setErro(
          await mensagemErro(resposta, "Não foi possível excluir a integração"),
        );
        return;
      }

      carregar(fonteId);
    } catch (erro) {
      setErro(mensagemExcecao(erro));
    }
  }

  function endpointWebhook(integracao: Integracao) {
    if (!integracao.webhookToken) {
      return "Token indisponível";
    }

    return `${window.location.origin}/api/webhooks/${integracao.webhookToken}`;
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

            <label>
              Tipo de coleta
              <select value={tipo} onChange={(e) => alterarTipo(e.target.value)}>
                <option value="API">API Basic</option>
                <option value="WEBHOOK">Webhook</option>
              </select>
            </label>

            {tipo === "API" && (
              <>
                <input
                  placeholder="URL da API"
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  required
                />

                <input
                  placeholder="Usuário"
                  value={usuario}
                  onChange={(e) => setUsuario(e.target.value)}
                  required
                />

                <input
                  type="password"
                  placeholder={
                    editandoId
                      ? "Segredo (vazio mantém o atual)"
                      : "Segredo"
                  }
                  value={segredo}
                  onChange={(e) => setSegredo(e.target.value)}
                  required={!editandoId}
                />

                <textarea
                  placeholder="Mapeamento JSON"
                  value={mapeamento}
                  onChange={(e) => setMapeamento(e.target.value)}
                  required
                />
              </>
            )}

            {tipo === "WEBHOOK" && (
              <p>
                O endereço do webhook será exibido na lista após salvar a
                integração.
              </p>
            )}

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
              <th>URL ou endpoint</th>
              <th>Autenticação</th>
              <th>Usuário</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {integracoes.map((integracao) => (
              <tr key={integracao.id}>
                <td>{integracao.tipo === "API" ? "API Basic" : "Webhook"}</td>
                <td>
                  {integracao.tipo === "WEBHOOK"
                    ? endpointWebhook(integracao)
                    : integracao.url || "—"}
                </td>
                <td>{integracao.tipo === "API" ? "Basic" : "—"}</td>
                <td>{integracao.tipo === "API" ? integracao.usuario || "—" : "—"}</td>
                <td>{integracao.ativo ? "Ativa" : "Inativa"}</td>
                <td>
                  <button onClick={() => editar(integracao)}>Editar</button>
                  <button onClick={() => excluir(integracao.id)}>Excluir</button>
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