package com.uberpb.service;

import com.uberpb.model.Localizacao;
import java.util.HashMap;
import java.util.Map;

public class ServicoRotaMock {
    private final Map<String, Localizacao> ruas = new HashMap<>();

    public ServicoRotaMock() {
        ruas.put("Rua das Acácias", new Localizacao(-7.115, -34.86));
        ruas.put("Avenida Epitácio Pessoa", new Localizacao(-7.125, -34.87));
        ruas.put("Rua da Mata", new Localizacao(-7.135, -34.88));
        ruas.put("Avenida Beira Rio", new Localizacao(-7.145, -34.89));
        ruas.put("Rua do Sol", new Localizacao(-7.155, -34.90));
    }

    public Localizacao geocodificar(String nomeRua) {
        return ruas.get(nomeRua);
    }

    public boolean rotaExiste(String origem, String destino) {
        return ruas.containsKey(origem) && ruas.containsKey(destino);
    }
}
