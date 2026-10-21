import { useState } from "react";

function Login({ aoEntrar }: { aoEntrar: (email: string) => void }) {
  const [email, setEmail] = useState("");

  return (
    <div className="login">
      <h1>MSP BI</h1>
      <p>Digite seu e-mail para receber o código de acesso.</p>
      <input
        placeholder="E-mail"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <button onClick={() => aoEntrar(email)}>Entrar</button>
    </div>
  );
}

export default Login;
