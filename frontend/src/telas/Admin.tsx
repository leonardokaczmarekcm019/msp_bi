import { useState } from "react";
import Usuarios from "./Usuarios";
import Organizacoes from "./Organizacoes";
import FontesDados from "./FontesDados";
import Integracoes from "./Integracoes";
import Geral from "./Geral";

type AdminProps = {
  autorizacao: string;
};

function Admin({ autorizacao }: AdminProps) {
  const [aba, setAba] = useState("usuarios");

  return (
    <div>
      <h1>Administração</h1>

      <nav>
        <button
          className={aba === "geral" ? "aba ativa" : "aba"}
          onClick={() => setAba("geral")}
        >
          Geral
        </button>
        <button
          className={aba === "usuarios" ? "aba ativa" : "aba"}
          onClick={() => setAba("usuarios")}
        >
          Usuários
        </button>
        <button
          className={aba === "organizacoes" ? "aba ativa" : "aba"}
          onClick={() => setAba("organizacoes")}
        >
          Organizações
        </button>
        <button
          className={aba === "fontes" ? "aba ativa" : "aba"}
          onClick={() => setAba("fontes")}
        >
          Fontes de Dados
        </button>
        <button
          className={aba === "integracoes" ? "aba ativa" : "aba"}
          onClick={() => setAba("integracoes")}
        >
          Integrações
        </button>
      </nav>

      {aba === "geral" && <Geral autorizacao={autorizacao} />}
      {aba === "usuarios" && <Usuarios autorizacao={autorizacao} />}
      {aba === "organizacoes" && <Organizacoes autorizacao={autorizacao} />}
      {aba === "fontes" && <FontesDados autorizacao={autorizacao} />}
      {aba === "integracoes" && <Integracoes autorizacao={autorizacao} />}
    </div>
  );
}

export default Admin;