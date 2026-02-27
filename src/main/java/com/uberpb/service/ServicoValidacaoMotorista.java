package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioUsuario;

import java.util.ArrayList;
import java.util.List;

/**
 * RF02: valida documentos do motorista e define as categorias do veículo.
 * Regras simples (ajuste depois se quiser):
 *  - UberX: todo veículo válido entra.
 *  - Comfort: ano >= 2018 e capacidade >= 4.
 *  - Black: ano >= 2020 e cor preta (ou "preto"/"black").
 *  - Bag: porta-malas "G".
 *  - XL: capacidade >= 6.
 * Conta fica ATIVA se CNH & CRLV forem válidos e houver ao menos 1 categoria.
 */
public class ServicoValidacaoMotorista {

    private final RepositorioUsuario repositorioUsuario;

    public ServicoValidacaoMotorista(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /** Passo único para completar cadastro do motorista. */
    public Motorista registrarDocumentosEVeiculo(String emailMotorista,
                                                 boolean cnhValida,
                                                 boolean crlvValido,
                                                 Veiculo veiculo) {
        Usuario u = repositorioUsuario.buscarPorEmail(emailMotorista);
        if (!(u instanceof Motorista))
            throw new IllegalArgumentException("Usuário não é motorista.");

        Motorista m = (Motorista) u;

        // Define documentos
        m.setCnhValida(cnhValida);
        m.setCrlvValido(crlvValido);

        // Calcula categorias do veículo e injeta no objeto
        var categorias = derivarCategorias(veiculo);
        veiculo.setCategoriasDisponiveis(categorias);
        m.setVeiculo(veiculo);

        // Ativa conta se documentos ok e tem categoria
        boolean podeAtivar = cnhValida && crlvValido && !categorias.isEmpty();
        m.setContaAtiva(podeAtivar);

        repositorioUsuario.atualizar(m);
        return m;
    }

    /** Regras determinísticas para categorias. */
    private List<CategoriaVeiculo> derivarCategorias(Veiculo v) {
        List<CategoriaVeiculo> cats = new ArrayList<>();
        if (v == null) return cats;

        // UberX – sempre que tiver veículo
        cats.add(CategoriaVeiculo.UBERX);

        // Comfort – ano >= 2018 e 4+ lugares
        if (v.getAno() >= 2018 && v.getCapacidadePassageiros() >= 4) {
            adicionarSeNaoTem(cats, CategoriaVeiculo.COMFORT);
        }

        // Black – ano >= 2020 e cor "preto"/"black"
        String cor = v.getCor() == null ? "" : v.getCor().toLowerCase();
        if (v.getAno() >= 2020 && (cor.contains("preto") || cor.contains("black"))) {
            adicionarSeNaoTem(cats, CategoriaVeiculo.BLACK);
        }

        // Bag – porta-malas "G"
        String malas = v.getTamanhoPortaMalas() == null ? "" : v.getTamanhoPortaMalas().toUpperCase();
        if (malas.equals("G")) {
            adicionarSeNaoTem(cats, CategoriaVeiculo.BAG);
        }

        // XL – 6+ lugares
        if (v.getCapacidadePassageiros() >= 6) {
            adicionarSeNaoTem(cats, CategoriaVeiculo.XL);
        }

        return cats;
    }

    private void adicionarSeNaoTem(List<CategoriaVeiculo> l, CategoriaVeiculo c) {
        for (CategoriaVeiculo e : l) if (e == c) return;
        l.add(c);
    }
}
