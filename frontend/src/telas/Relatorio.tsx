import { useState } from "react";
import RelatorioChamados from "./RelatorioChamados";
import RelatorioTarefas from "./RelatorioTarefas";
import RelatorioAlertas from "./RelatorioAlertas";

function Relatorio({ autorizacao }: { autorizacao: string }) {
  const [aba, setAba] = useState("chamados");

  return (
    <div>
      <h1>Relatórios</h1>

      <nav>
        <button
          className={aba === "chamados" ? "aba ativa" : "aba"}
          onClick={() => setAba("chamados")}
        >
          Chamados
        </button>

        <button
          className={aba === "tarefas" ? "aba ativa" : "aba"}
          onClick={() => setAba("tarefas")}
        >
          Tarefas
        </button>

        <button
          className={aba === "alertas" ? "aba ativa" : "aba"}
          onClick={() => setAba("alertas")}
        >
          Alertas
        </button>
      </nav>

      {aba === "chamados" && <RelatorioChamados autorizacao={autorizacao} />}
      {aba === "tarefas" && <RelatorioTarefas autorizacao={autorizacao} />}
      {aba === "alertas" && <RelatorioAlertas autorizacao={autorizacao} />}
    </div>
  );
}

export default Relatorio;