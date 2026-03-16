package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class VisualizarCarrinhoComandoTest {

    private VisualizarCarrinhoComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private ServicoCarrinho servicoCarrinho;
    private ServicoPedido servicoPedido;
    private ServicoEntrega servicoEntrega;

    @BeforeEach
    void setup() {
        comando = new VisualizarCarrinhoComando();

        sessao = mock(Sessao.class);
        servicoCarrinho = mock(ServicoCarrinho.class);
        servicoPedido = mock(ServicoPedido.class);
        servicoEntrega = mock(ServicoEntrega.class);

        Usuario usuario = mock(Passageiro.class);
        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("cliente@email.com");

        // Criando contexto mínimo necessário
        contexto = new ContextoAplicacao(
                sessao,
                mock(RepositorioUsuario.class),
                mock(RepositorioRestaurante.class),
                mock(ServicoCadastro.class),
                mock(ServicoAutenticacao.class),
                mock(com.uberpb.repository.RepositorioCorrida.class),
                mock(ServicoCorrida.class),
                mock(com.uberpb.repository.RepositorioOferta.class),
                mock(com.uberpb.repository.RepositorioAvaliacao.class),
                mock(ServicoOferta.class),
                mock(ServicoValidacaoMotorista.class),
                mock(ServicoPagamento.class),
                mock(ServicoAvaliacao.class),
                mock(ServicoOtimizacaoRota.class),
                mock(ServicoLocalizacao.class),
                mock(ServicoDirecionamentoCorrida.class),
                mock(EstimativaChegada.class),
                mock(ServicoAdmin.class),
                mock(GerenciadorCorridasAtivas.class),
                servicoCarrinho,
                mock(com.uberpb.repository.RepositorioPedido.class),
                servicoPedido,
                mock(com.uberpb.repository.RepositorioNotificacao.class),
                mock(ServicoNotificacao.class),
                servicoEntrega
        );
    }

    @Test
    void carrinhoVazio() {
        when(servicoCarrinho.obterCarrinho("cliente@email.com")).thenReturn(mock(CarrinhoCompras.class));
        CarrinhoCompras carrinho = servicoCarrinho.obterCarrinho("cliente@email.com");
        when(carrinho.isEmpty()).thenReturn(true);

        comando.executar(contexto, new Scanner("\n"));
        verify(carrinho).isEmpty();
    }

    @Test
    void removerItemDoCarrinho() {
        ItemCarrinho item = mock(ItemCarrinho.class);
        Restaurante restaurante = mock(Restaurante.class);
        when(restaurante.getNomeFantasia()).thenReturn("Teste");
        when(restaurante.getTaxaEntrega()).thenReturn(5.0);

        CarrinhoCompras carrinho = mock(CarrinhoCompras.class);
        when(carrinho.isEmpty()).thenReturn(false);
        when(carrinho.getItens()).thenReturn(List.of(item));
        when(carrinho.getRestaurante()).thenReturn(restaurante);
        when(carrinho.getTotalItens()).thenReturn(20.0);
        when(carrinho.getTotalGeral()).thenReturn(25.0);

        when(servicoCarrinho.obterCarrinho("cliente@email.com")).thenReturn(carrinho);

        // Simula opção 1 = remover item, depois 0 = sair
        Scanner scanner = new Scanner("1\n1\n0\n");
        comando.executar(contexto, scanner);

        verify(carrinho).removerItemPorIndice(0);
    }

    @Test
    void finalizarPedidoImediatoConfirmado() {
        executarFluxoFinalizarPedido("2\n1\n1\n1\n", TipoPedido.IMEDIATO, true);
    }

    @Test
    void finalizarPedidoImediatoCancelado() {
        executarFluxoFinalizarPedido("2\n1\n1\n2\n", TipoPedido.IMEDIATO, false);
    }

    @Test
    void finalizarPedidoAgendadoConfirmado() {
        LocalDateTime agendamento = LocalDateTime.now().plusDays(1);
        executarFluxoFinalizarPedido("2\n2\n" + agendamento.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n1\n1\n", TipoPedido.AGENDADO, true);
    }

    @Test
    void finalizarPedidoAgendadoCancelado() {
        LocalDateTime agendamento = LocalDateTime.now().plusDays(1);
        executarFluxoFinalizarPedido("2\n2\n" + agendamento.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n1\n2\n", TipoPedido.AGENDADO, false);
    }

    // Método auxiliar para reduzir repetição de código
    private void executarFluxoFinalizarPedido(String entradas, TipoPedido tipoPedidoEsperado, boolean confirmar) {

        ItemCarrinho item = mock(ItemCarrinho.class);
        Restaurante restaurante = mock(Restaurante.class);
        when(restaurante.getNomeFantasia()).thenReturn("Teste");
        when(restaurante.getTaxaEntrega()).thenReturn(5.0);
        when(restaurante.getEmail()).thenReturn("restaurante@email.com");

        CarrinhoCompras carrinho = mock(CarrinhoCompras.class);
        when(carrinho.isEmpty()).thenReturn(false);
        when(carrinho.getItens()).thenReturn(List.of(item));
        when(carrinho.getRestaurante()).thenReturn(restaurante);
        when(carrinho.getTotalItens()).thenReturn(20.0);
        when(carrinho.getTotalGeral()).thenReturn(25.0);

        when(servicoCarrinho.obterCarrinho("cliente@email.com")).thenReturn(carrinho);

        Scanner scanner = new Scanner(entradas);
        comando.executar(contexto, scanner);

        if (confirmar) {
            verify(servicoPedido).salvarPedido(any());
            verify(carrinho).limpar();
        } else {
            verify(servicoPedido, never()).salvarPedido(any());
            verify(carrinho, never()).limpar();
        }
    }
}
