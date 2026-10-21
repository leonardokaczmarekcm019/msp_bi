const cruds = [
  "Organizações",
  "Usuários",
  "Fontes de Dados",
];

function Admin() {
  return (
    <div>
      <h1>Administração</h1>
      {cruds.map((nome) => (
        <div key={nome}>
          <button>{nome}</button>
        </div>
      ))}
    </div>
  );
}

export default Admin;
