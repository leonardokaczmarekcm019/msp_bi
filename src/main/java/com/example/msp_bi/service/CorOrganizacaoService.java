package com.example.msp_bi.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CorOrganizacaoService {

    public static final String COR_PADRAO = "#3b82f6";
    private static final Path ARQUIVO = Path.of("dados", "cores-organizacoes.properties");

    private final Map<Long, String> cores = new ConcurrentHashMap<>();

    public CorOrganizacaoService() {
        if (Files.exists(ARQUIVO)) {
            try (Reader leitor = Files.newBufferedReader(ARQUIVO)) {
                Properties props = new Properties();
                props.load(leitor);
                props.forEach((id, cor) -> cores.put(Long.valueOf((String) id), (String) cor));
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler as cores das organizações", e);
            }
        }
    }

    public String obter(Long id) {
        return cores.getOrDefault(id, COR_PADRAO);
    }

    public Map<Long, String> todas() {
        return Map.copyOf(cores);
    }

    public synchronized void definir(Long id, String cor) {
        cores.put(id, cor);
        salvar();
    }

    public synchronized void remover(Long id) {
        if (cores.remove(id) != null) {
            salvar();
        }
    }

    private void salvar() {
        Properties props = new Properties();
        cores.forEach((id, cor) -> props.setProperty(String.valueOf(id), cor));
        try {
            Files.createDirectories(ARQUIVO.getParent());
            try (Writer escritor = Files.newBufferedWriter(ARQUIVO)) {
                props.store(escritor, "Cores das organizações");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar a cor da organização", e);
        }
    }
}