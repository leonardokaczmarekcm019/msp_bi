import { useEffect, useState, type FormEvent } from "react";

type TipoUsuario = {
  id: number;
  nome: string;
};

type Usuario = {
  id: number;
  nome: string;
  email: string;
  numero: string;
  ativo: boolean;
  tipoUsuario: TipoUsuario | null;
};

function Usuarios() {
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

  async function carregar() {
    try {
      const resposta = await fetch("/api/usuarios");

      if (!resposta.ok) {
        throw new Error();
      }

      setUsuarios(await resposta.json());
    } catch {
      setErro("Não foi possível carregar os usuários.");
    } finally {
      setCarregando(false);
    }
  }

  async function carregarTipos() {
    try {
      const resposta = await fetch("/api/tipos-usuario");

      if (!resposta.ok) {
        throw new Error();
      }

      setTipos(await resposta.json());
    } catch {
      setErro("Não foi possível carregar os tipos de usuário.");
    }
  }

  useEffect(() => {
    carregar();
    carregarTipos();
  }, []);

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

    const resposta = await fetch(url, {
      method: editandoId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nome,
        email,
        numero,
        tipoUsuario: tipoId ? { id: Number(tipoId) } : null,
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

  function editar(usuario: Usuario) {
    setErro("");
    setEditandoId(usuario.id);
    setNome(usuario.nome);
    setEmail(usuario.email);
    setNumero(usuario.numero);
    setTipoId(usuario.tipoUsuario ? String(usuario.tipoUsuario.id) : "");
    setAtivo(usuario.ativo);
    setModalAberto(true);
  }

  async function excluir(id: number) {
    if (!window.confirm("Deseja excluir este usuário?")) {
      return;
    }

    setErro("");

    const resposta = await fetch(`/api/usuarios/${id}`, {
      method: "DELETE",
    });

    if (!resposta.ok) {
      setErro("Não foi possível excluir. Ele pode estar em uso por uma tarefa.");
      return;
    }

    carregar();
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
              required
            />
            <select value={tipoId} onChange={(e) => setTipoId(e.target.value)}>
              <option value="">Sem tipo de usuário</option>
              {tipos.map((tipo) => (
                <option key={tipo.id} value={tipo.id}>
                  {tipo.nome}
                </option>
              ))}
            </select>
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
