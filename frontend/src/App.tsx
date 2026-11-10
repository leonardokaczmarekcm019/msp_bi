import { useState } from "react";
import Login from "./telas/Login";
import Dashboard from "./telas/Dashboard";
import Relatorio from "./telas/Relatorio";
import Admin from "./telas/Admin";
import "./App.css";

function App() {
  const [tela, setTela] = useState("login");
  const [autorizacao, setAutorizacao] = useState("");
  const [ehAdmin, setEhAdmin] = useState(false);

  function entrar(cabecalho: string, gerente: boolean) {
    setAutorizacao(cabecalho);
    setEhAdmin(gerente);
    setTela("dashboard");
  }

  function sair() {
    setAutorizacao("");
    setTela("login");
    setEhAdmin(false);
  }

  if (tela === "login") {
    return <Login aoEntrar={entrar} />;
  }

  return (
    <div>
      <nav className="menu">
        <button className={tela === "dashboard" ? "ativo" : ""} onClick={() => setTela("dashboard")}>Dashboard</button>
        <button className={tela === "relatorio" ? "ativo" : ""} onClick={() => setTela("relatorio")}>Relatório</button>
        {ehAdmin && <button className={tela === "admin" ? "ativo" : ""} onClick={() => setTela("admin")}>Admin</button>}
        <button onClick={sair}>Sair</button>
      </nav>

      <main>
        {tela === "dashboard" && autorizacao && <Dashboard autorizacao={autorizacao} />}
        {tela === "relatorio" && autorizacao && <Relatorio autorizacao={autorizacao} />}
        {tela === "admin" && autorizacao && ehAdmin && (<Admin autorizacao={autorizacao} />)}
      </main>
    </div>
  );
}

export default App;
