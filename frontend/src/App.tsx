import { useState } from "react";
import Login from "./telas/Login";
import Dashboard from "./telas/Dashboard";
import Relatorio from "./telas/Relatorio";
import Admin from "./telas/Admin";
import "./App.css";

function App() {
  const [tela, setTela] = useState("login");
  const [email, setEmail] = useState("");

  function entrar(emailDigitado: string) {
    setEmail(emailDigitado);
    setTela("dashboard");
  }

  // E-mails que começam com "admin" considerados como administradores
  const ehAdmin = email.toLowerCase().startsWith("admin");

  if (tela === "login") {
    return <Login aoEntrar={entrar} />;
  }

  return (
    <div>
      <nav className="menu">
        <button onClick={() => setTela("dashboard")}>Dashboard</button>
        <button onClick={() => setTela("relatorio")}>Relatório</button>
        {ehAdmin && <button onClick={() => setTela("admin")}>Admin</button>}
        <button onClick={() => setTela("login")}>Sair</button>
      </nav>

      <main>
        {tela === "dashboard" && <Dashboard />}
        {tela === "relatorio" && <Relatorio />}
        {tela === "admin" && ehAdmin && <Admin />}
      </main>
    </div>
  );
}

export default App;
