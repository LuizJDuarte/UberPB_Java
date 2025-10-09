package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServicoCorridaTest {

    @Test
    void estimativaRetornaPrecoPositivo() {
        var sc = new ServicoCorrida(new ImplRepositorioCorridaArquivo(), new ImplRepositorioUsuarioArquivo());
        var est = sc.estimarPorEnderecos("Rua A", "Rua B", CategoriaVeiculo.UBERX);
        // se sua classe EstimativaCorrida tiver outro getter (ex.: getPrecoEstimado), mude esta linha:
        assertTrue(est.getPreco() > 0);
    }

    @Test
    void origemIgualDestinoDeveFalhar() {
        var sc = new ServicoCorrida(new ImplRepositorioCorridaArquivo(), new ImplRepositorioUsuarioArquivo());
        assertThrows(IllegalArgumentException.class, () ->
                sc.solicitarCorrida("passageiro@gmail.com", "X", "X",
                        CategoriaVeiculo.UBERX, MetodoPagamento.DINHEIRO));
    }
}
