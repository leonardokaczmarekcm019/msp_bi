import { useCallback, useEffect, useState, type FormEvent } from "react";
import { mensagemErro, mensagemExcecao, requisicao } from "../api";

type Organizacao = {
    id: number;
    nome: string;
    cnpj: string | null;
    email: string | null;
    telefone: string | null;
    logo: string | null;
    ativo: boolean;
};

type OrganizacoesProps = {
  autorizacao: string;
};

function Organizacoes({ autorizacao }: OrganizacoesProps) {
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
    const [cores, setCores] = useState<Record<number, string>>({});

    const carregar = useCallback(() => {
        requisicao("/api/organizacoes", autorizacao)
            .then((resposta) => {
                if (!resposta.ok) throw new Error();
                return resposta.json();
            })
            .then(setOrganizacoes)
            .catch(() => setErro("Não foi possível carregar as organizações."))
            .finally(() => setCarregando(false));
    }, [autorizacao]);

    const carregarCores = useCallback(() => {
        requisicao("/api/cores-organizacao", autorizacao)
            .then((resposta) => {
                if (!resposta.ok) throw new Error();
                return resposta.json();
            })
            .then(setCores)
            .catch(() => setErro("Não foi possível carregar as cores das organizações."));
    }, [autorizacao]);

    async function alterarCor(id: number, cor: string) {
        setCores((atuais) => ({ ...atuais, [id]: cor }));

        try {
            const resposta = await requisicao(`/api/organizacoes/${id}/cor`, autorizacao, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ cor }),
            });

            if (!resposta.ok) {
                setErro(await mensagemErro(resposta, "Erro ao salvar a cor"));
                carregarCores();
            }
        } catch (erro) {
            setErro(mensagemExcecao(erro));
            carregarCores();
        }
    }

    useEffect(() => {
        carregar();
        carregarCores();
    }, [carregar, carregarCores]);

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

        try {
            const resposta = await requisicao(url, autorizacao, {
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
                setErro(await mensagemErro(resposta, "Erro ao salvar"));
                return;
            }

            limparFormulario();
            carregar();
        } catch (erro) {
            setErro(mensagemExcecao(erro));
        }
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

        try {
            const resposta = await requisicao(`/api/organizacoes/${id}`, autorizacao, {
                method: "DELETE",
            });

            if (!resposta.ok) {
                setErro(await mensagemErro(resposta, "Não foi possível excluir a organização"));
                return;
            }

            carregar();
        } catch (erro) {
            setErro(mensagemExcecao(erro));
        }
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
                            <th>Cor</th>
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
                                    <input
                                        type="color"
                                        value={cores[organizacao.id] || "#3b82f6"}
                                        onChange={(e) => alterarCor(organizacao.id, e.target.value)}
                                    />
                                </td>
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