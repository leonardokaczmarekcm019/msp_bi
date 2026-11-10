import { useCallback, useEffect, useState, type FormEvent } from "react";
import { mensagemErro, mensagemExcecao, requisicao } from "../api";

type TipoUsuario = {
  id: number;
  nome: string;
};

type Usuario = {
  id: number;
  nome: string;
  email: string;
  numero: string | null;
  ativo: boolean;
  tipoUsuario: TipoUsuario | null;
};

function Usuarios({ autorizacao }: { autorizacao: string }) {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [tipos, setTipos] = useState<TipoUsuario[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [numero, setNumero] = useState("");
  const [tipoId, setTipoId] = useState("");
  const [ativo, setAtivo] = useState(true);

  const carregar = useCallback(() => {
    requisicao("/api/usuarios", autorizacao)
      .then((resposta) => {
        if (!resposta.ok) throw new Error();
        return resposta.json();
      })
      .then(setUsuarios)
      .catch(() => setErro("Não foi possível carregar os usuários."))
      .finally(() => setCarregando(false));
  }, [autorizacao]);

  const carregarTipos = useCallback(() => {
    requisicao("/api/tipos-usuario", autorizacao)
      .then((resposta) => {
        if (!resposta.ok) throw new Error();
        return resposta.json();
      })
      .then(setTipos)
      .catch(() => setErro("Não foi possível carregar os tipos de usuário."));
  }, [autorizacao]);

  useEffect(() => {
    carregar();
    carregarTipos();
  }, [carregar, carregarTipos]);

  function limparFormulario() {
    setEditandoId(null);
    setNome("");
    setEmail("");
    setNumero("");
    setTipoId("");
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

    const url = editandoId ? `/api/usuarios/${editandoId}` : "/api/usuarios";

    try {
      const resposta = await requisicao(url, autorizacao, {
        method: editandoId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nome,
          email,
          numero: numero || null,
          tipoUsuario: tipoId ? { id: Number(tipoId) } : null,
          ativo,
        }),
      });

      if (!resposta.ok) {
        setErro(await mensagemErro(resposta, "Erro ao salvar"));
        return;
      }

      limparFormulario();
      carregar();
    } catch (erro) {
      setErro(mensagemExcecao(erro));
    }
  }

  function editar(usuario: Usuario) {
    setErro("");
    setEditandoId(usuario.id);
    setNome(usuario.nome);
    setEmail(usuario.email);
    setNumero(usuario.numero ?? "");
    setTipoId(usuario.tipoUsuario ? String(usuario.tipoUsuario.id) : "");
    setAtivo(usuario.ativo);
    setModalAberto(true);
  }

  async function excluir(id: number) {
    if (!window.confirm("Deseja excluir este usuário?")) {
      return;
    }

    setErro("");

    try {
      const resposta = await requisicao(`/api/usuarios/${id}`, autorizacao, {
        method: "DELETE",
      });

      if (!resposta.ok) {
        setErro(await mensagemErro(resposta, "Não foi possível excluir o usuário"));
        return;
      }

      carregar();
    } catch (erro) {
      setErro(mensagemExcecao(erro));
    }
  }

  return (
    <div>
      <h2>Usuários</h2>

      <button onClick={novo}>Novo usuário</button>

      {modalAberto && (
        <div className="modal-fundo">
          <form className="modal" onSubmit={salvar}>
            <h3>{editandoId ? "Editar usuário" : "Novo usuário"}</h3>

            <input
              placeholder="Nome"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
            <input
              placeholder="E-mail"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              placeholder="Número"
              value={numero}
              onChange={(e) => setNumero(e.target.value)}
            />
            {editandoId !== null && (
              <select value={tipoId} onChange={(e) => setTipoId(e.target.value)}>
                <option value="">Sem tipo de usuário</option>
                {tipos.map((tipo) => (
                  <option key={tipo.id} value={tipo.id}>
                    {tipo.nome}
                  </option>
                ))}
              </select>
            )}
            <label>
              <input
                type="checkbox"
                checked={ativo}
                onChange={(e) => setAtivo(e.target.checked)}
              />
              Ativo
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

      {!carregando && usuarios.length === 0 && <p>Nenhum usuário cadastrado.</p>}

      {usuarios.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>E-mail</th>
              <th>Número</th>
              <th>Tipo</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {usuarios.map((usuario) => (
              <tr key={usuario.id}>
                <td>{usuario.nome}</td>
                <td>{usuario.email}</td>
                <td>{usuario.numero}</td>
                <td>{usuario.tipoUsuario?.nome || "—"}</td>
                <td>{usuario.ativo ? "Ativo" : "Inativo"}</td>
                <td>
                  <button onClick={() => editar(usuario)}>Editar</button>
                  <button onClick={() => excluir(usuario.id)}>Excluir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Usuarios;
