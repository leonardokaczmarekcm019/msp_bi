import { useState } from "react";
import Usuarios from "./Usuarios";
import Organizacoes from "./Organizacoes";
import FontesDados from "./FontesDados";
import Integracoes from "./Integracoes";

function Admin() {
  const [aba, setAba] = useState("usuarios");

  return (
    <div>
      <h1>Administração</h1>

      <nav>
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

      {aba === "usuarios" && <Usuarios />}
      {aba === "organizacoes" && <Organizacoes />}
      {aba === "fontes" && <FontesDados />}
      {aba === "integracoes" && <Integracoes />}
    </div>
  );
}

export default Admin;