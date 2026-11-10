type TokenCsrf = {
  headerName: string;
  token: string;
};

export async function requisicao(
  url: string,
  autorizacao: string,
  opcoes: RequestInit = {},
): Promise<Response> {
  const headers = new Headers(opcoes.headers);
  headers.set("Authorization", autorizacao);

  const metodo = (opcoes.method ?? "GET").toUpperCase();
  if (!["GET", "HEAD", "OPTIONS", "TRACE"].includes(metodo)) {
    const resposta = await fetch("/api/csrf", {
      headers: { Authorization: autorizacao },
      cache: "no-store",
    });

    if (!resposta.ok) {
      throw new Error(`Não foi possível obter o token de segurança (${resposta.status}).`);
    }

    const csrf: TokenCsrf = await resposta.json();
    if (!csrf.headerName || !csrf.token) {
      throw new Error("O servidor retornou um token de segurança inválido.");
    }
    headers.set(csrf.headerName, csrf.token);
  }

  return fetch(url, { ...opcoes, headers });
}

export async function mensagemErro(resposta: Response, mensagem: string): Promise<string> {
  if (resposta.headers.get("content-type")?.includes("application/json")) {
    const dados: unknown = await resposta.json();
    if (
      typeof dados === "object" &&
      dados !== null &&
      "errors" in dados &&
      Array.isArray(dados.errors) &&
      dados.errors.every((erro: unknown) => typeof erro === "string") &&
      dados.errors.length > 0
    ) {
      return dados.errors.join(", ");
    }
  }
  return `${mensagem} (HTTP ${resposta.status}).`;
}

export function mensagemExcecao(erro: unknown): string {
  return erro instanceof Error && !(erro instanceof TypeError)
    ? erro.message
    : "Não foi possível conectar ao backend.";
}
