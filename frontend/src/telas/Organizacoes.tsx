import { useEffect, useState, type FormEvent } from "react";

type Organizacao = {
    id: number;
    nome: string;
    cnpj: string | null;
    email: string | null;
    telefone: string | null;
    logo: string | null;
    ativo: boolean;
};

function Organizacoes() {
    const [organizacoes, setOrganizacoes] = useState<Organizacao[]>([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");
    const [editandoId, setEditandoId] = useState<number | null>(null);
    const [modalAberto, setModalAberto] = useState(false);

    const [nome, setNome] = useState("");
    const [cnpj, setCnpj] = useState("");
    const [email, setEmail] = useState("");
    const [telefone, setTelefone] = useState("");
    const [ativo, setAtivo] = useState(true);

    async function carregar() {
        try {
            const resposta = await fetch("/api/organizacoes");

            if (!resposta.ok) {
                throw new Error();
            }

            setOrganizacoes(await resposta.json());
        } catch {
            setErro("Não foi possível carregar as organizações.");
        } finally {
            setCarregando(false);
        }
    }

    useEffect(() => {
        carregar();
    }, []);

    function limparFormulario() {
        setEditandoId(null);
        setNome("");
        setCnpj("");
        setEmail("");
        setTelefone("");
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

        const url = editandoId
            ? `/api/organizacoes/${editandoId}`
            : "/api/organizacoes";

        const resposta = await fetch(url, {
            method: editandoId ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                nome,
                cnpj: cnpj || null,
                email: email || null,
                telefone: telefone || null,
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

    function editar(organizacao: Organizacao) {
        setEditandoId(organizacao.id);
        setNome(organizacao.nome);
        setCnpj(organizacao.cnpj || "");
        setEmail(organizacao.email || "");
        setTelefone(organizacao.telefone || "");
        setAtivo(organizacao.ativo);
        setModalAberto(true);
    }

    async function excluir(id: number) {
        if (!window.confirm("Deseja excluir esta organização?")) {
            return;
        }

        setErro("");

        const resposta = await fetch(`/api/organizacoes/${id}`, {
            method: "DELETE",
        });

        if (!resposta.ok) {
            setErro(
                "Não foi possível excluir. Ela pode estar em uso por uma fonte de dados."
            );
            return;
        }

        carregar();
    }

    return (
        <div>
            <h2>Organizações</h2>

            <button onClick={novo}>Nova organização</button>

            {modalAberto && (
                <div className="modal-fundo">
                    <form className="modal" onSubmit={salvar}>
                        <h3>{editandoId ? "Editar organização" : "Nova organização"}</h3>

                        <input
                            placeholder="Nome"
                            value={nome}
                            onChange={(e) => setNome(e.target.value)}
                            required
                        />
                        <input
                            placeholder="CNPJ"
                            value={cnpj}
                            onChange={(e) => setCnpj(e.target.value)}
                        />
                        <input
                            placeholder="E-mail"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                        <input
                            placeholder="Telefone"
                            value={telefone}
                            onChange={(e) => setTelefone(e.target.value)}
                        />
                        <label>
                            <input
                                type="checkbox"
                                checked={ativo}
                                onChange={(e) => setAtivo(e.target.checked)}
                            />
                            Ativa
                        </label>

                        {!modalAberto && erro && <p className="erro">{erro}</p>}

                        <div>
                            <button type="submit">Salvar</button>
                            <button type="button" onClick={limparFormulario}>
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {erro && <p className="erro">{erro}</p>}
            {carregando && <p>Carregando...</p>}

            {!carregando && organizacoes.length === 0 && (
                <p>Nenhuma organização cadastrada.</p>
            )}

            {organizacoes.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>E-mail</th>
                            <th>CNPJ</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {organizacoes.map((organizacao) => (
                            <tr key={organizacao.id}>
                                <td>{organizacao.nome}</td>
                                <td>{organizacao.email || "—"}</td>
                                <td>{organizacao.cnpj || "—"}</td>
                                <td>{organizacao.ativo ? "Ativa" : "Inativa"}</td>
                                <td>
                                    <button onClick={() => editar(organizacao)}>Editar</button>
                                    <button onClick={() => excluir(organizacao.id)}>
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

export default Organizacoes;