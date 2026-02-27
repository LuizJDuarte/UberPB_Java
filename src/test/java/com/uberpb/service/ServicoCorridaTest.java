package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ServicoCorridaTest {

    @Test
    public void testSolicitarCorrida() {
        RepositorioCorrida repoCorrida = mock(RepositorioCorrida.class);
        RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
        EstimativaCorrida estimativaCorrida = mock(EstimativaCorrida.class);
        ServicoLocalizacao servicoLocalizacao = mock(ServicoLocalizacao.class);
        ServicoCorrida servicoCorrida = new ServicoCorrida(repoCorrida, repoUsuario, estimativaCorrida, servicoLocalizacao);

        when(repoUsuario.buscarPorEmail("passageiro@email.com")).thenReturn(new com.uberpb.model.Passageiro("passageiro@email.com", "senha"));

        servicoCorrida.solicitarCorrida("passageiro@email.com", "origem", "destino", CategoriaVeiculo.BLACK, MetodoPagamento.PIX);
        verify(repoCorrida).salvar(any(Corrida.class));
    }
}
