package com.example.msp_bi.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class MapeamentoApi {

    private MapeamentoApi() {
    }

    static void validar(Map<?, ?> mapeamento) {
        validarCampos(mapeamento, "mapeamento");
    }

    private static void validarCampos(Map<?, ?> campos, String caminho) {
        for (Map.Entry<?, ?> entrada : campos.entrySet()) {
            if (!(entrada.getKey() instanceof String destino) || destino.isBlank()) {
                throw new IllegalArgumentException(caminho + ": nome de destino inválido");
            }
            Object regra = entrada.getValue();
            if (regra instanceof String origem && !origem.isBlank()) {
                continue;
            }
            if (!(regra instanceof Map<?, ?> objeto)
                    || objeto.size() != 2
                    || !(objeto.get("campo") instanceof String origem)
                    || origem.isBlank()
                    || !(objeto.get("campos") instanceof Map<?, ?> subcampos)) {
                throw new IllegalArgumentException(caminho + "." + destino
                        + " deve ser um nome de campo ou um objeto com campo e campos");
            }
            validarCampos(subcampos, caminho + "." + destino);
        }
    }

    static Map<String, Object> aplicar(Map<?, ?> origem, Map<?, ?> campos) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entrada : campos.entrySet()) {
            String destino = (String) entrada.getKey();
            Object regra = entrada.getValue();
            if (regra instanceof String campo) {
                resultado.put(destino, origem.get(campo));
            } else {
                Map<?, ?> objeto = (Map<?, ?>) regra;
                String campo = (String) objeto.get("campo");
                Map<?, ?> subcampos = (Map<?, ?>) objeto.get("campos");
                Object valor = origem.get(campo);
                resultado.put(destino, aplicarAninhado(valor, subcampos, destino));
            }
        }
        return resultado;
    }

    private static Object aplicarAninhado(Object valor, Map<?, ?> campos, String destino) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Map<?, ?> objeto) {
            return aplicar(objeto, campos);
        }
        if (valor instanceof List<?> itens) {
            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Object item : itens) {
                if (!(item instanceof Map<?, ?> objeto)) {
                    throw new IllegalArgumentException(destino + " deve conter apenas objetos");
                }
                resultado.add(aplicar(objeto, campos));
            }
            return resultado;
        }
        throw new IllegalArgumentException(destino + " deve ser um objeto ou uma lista de objetos");
    }
}
