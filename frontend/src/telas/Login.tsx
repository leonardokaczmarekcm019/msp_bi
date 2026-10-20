import { useEffect, useState } from "react";

interface Login {
  id: number;
  nome: string;
}

export default function Login() {
  const [itens, setItens] = useState<Login[]>([]);
  const [nome, setNome] = useState("");

  useEffect(() => {
    // Troque pela URL real do seu controller Spring
    fetch("http://localhost:8080/login")
      .then((r) => r.json())
      .then(setItens)
      .catch(console.error);
  }, []);

  function adicionar(e: React.FormEvent) {
    e.preventDefault();
    if (!nome.trim()) return;
    setItens([...itens, { id: Date.now(), nome }]);
    setNome("");
  }

  return (
    <div>
      <h1>Login</h1>

      <form onSubmit={adicionar}>
        <input
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          placeholder="Nome"
        />
        <button type="submit">Adicionar</button>
      </form>

      <ul>
        {itens.map((o) => (
          <li key={o.id}>{o.nome}</li>
        ))}
      </ul>
    </div>
  );
}