import { useState, type FormEvent } from "react";

type LoginProps = {
  aoEntrar: (autorizacao: string, gerente: boolean) => void;
};

function Login({ aoEntrar }: LoginProps) {
  const [email, setEmail] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function enviarFormulario(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    setMensagem("");

    const emailInformado = email.trim();

    if (!emailInformado) {
      setMensagem("Informe o e-mail.");
      return;
    }

    // Valor apenas para o formato Basic; não funciona como senha.
    const autorizacao = `Basic ${btoa(`${emailInformado}:prototipo`)}`;

    setCarregando(true);

    try {
      const resposta = await fetch("/api/sessao", {
        headers: {
          Authorization: autorizacao,
          Accept: "application/json",
          "X-Requested-With": "XMLHttpRequest",
        },
        cache: "no-store",
      });

      if (resposta.status === 401 || resposta.status === 403) {
        setMensagem("Usuário não encontrado ou sem permissão para acesso.");
        return;
      }

      if (!resposta.ok) {
        setMensagem(`Erro ao acessar a API: ${resposta.status}.`);
        return;
      }

      const sessao: { email: string; gerente: boolean } = await resposta.json();
      aoEntrar(autorizacao, sessao.gerente);
    } catch {
      setMensagem("Não foi possível conectar ao backend.");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <form className="login" onSubmit={enviarFormulario}>
      <h1>MSP BI</h1>
      <p>Protótipo local: informe seu e-mail para entrar.</p>

      <label>
        E-mail
        <input
          type="email"
          value={email}
          onChange={(evento) => setEmail(evento.target.value)}
          autoComplete="email"
          required
        />
      </label>

      <button type="submit" disabled={carregando}>
        {carregando ? "Verificando..." : "Entrar"}
      </button>

      {mensagem && <p className="erro" role="alert">{mensagem}</p>}
    </form>
  );
}

export default Login;