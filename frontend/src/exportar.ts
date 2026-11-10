export function baixarCsv(nome: string, linhas: (string | number | null | undefined)[][]) {
  const texto = linhas
    .map((l) => l.map((c) => `"${String(c ?? "").replace(/"/g, '""')}"`).join(";"))
    .join("\n");

  const arquivo = new Blob(["\uFEFF" + texto], { type: "text/csv;charset=utf-8" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(arquivo);
  link.download = nome + ".csv";
  link.click();
  URL.revokeObjectURL(link.href);
}