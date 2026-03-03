package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Pedido;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * RF24: Comando para entregador aceitar um pedido de entrega
 */
public class AceitarPedidoComando implements Comando {

    @Override
    public String nome() {
        return "[Entregador] Aceitar Pedido";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Entregador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        Usuario usuario = contexto.sessao.getUsuarioAtual();

        if (!(usuario instanceof Entregador entregador)) {
            System.out.println("⚠️ Este comando é apenas para entregadores.");
            return;
        }

        if (!entregador.isContaAtiva()) {
            System.out.println("⚠️ Sua conta ainda não foi ativada pelo administrador.");
            return;
        }

        if (!entregador.isDisponivel()) {
            System.out.println("⚠️ Você precisa estar online para aceitar pedidos.");
            return;
        }

        System.out.println("\n========== ACEITAR PEDIDO ==========");

        List<Pedido> pedidosDisponiveis = contexto.servicoPedido
                .buscarPedidosDisponiveisParaEntregador(entregador.getEmail());

        if (pedidosDisponiveis.isEmpty()) {
            System.out.println("(Nenhum pedido disponível para aceitar)");
            System.out.println("====================================\n");
            return;
        }

        // Exibe os pedidos disponíveis
        int contador = 1;
        for (Pedido pedido : pedidosDisponiveis) {
            System.out.printf("\n[%d] Pedido:\n", contador++);
            System.out.printf("    Restaurante: %s\n", pedido.getEmailRestaurante());
            System.out.printf("    Cliente: %s\n", pedido.getEmailCliente());
            System.out.printf("    Total: R$ %.2f\n", pedido.getTotal());
            System.out.printf("    Taxa de Entrega: R$ %.2f\n", pedido.getTotal() * 0.15);
            System.out.printf("    Status: %s\n", pedido.getStatus());
        }

        System.out.println("\n====================================");
        System.out.print("\nDigite o número do pedido que deseja aceitar (ou 0 para cancelar): ");

        int escolha;
        try {
            escolha = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Número inválido.");
            return;
        }

        if (escolha == 0) {
            System.out.println("Operação cancelada.");
            return;
        }

        if (escolha < 1 || escolha > pedidosDisponiveis.size()) {
            System.out.println("❌ Número de pedido inválido.");
            return;
        }

        Pedido pedidoSelecionado = pedidosDisponiveis.get(escolha - 1);

        // Confirmação
        System.out.printf("\nConfirma aceitar o pedido do restaurante %s? (s/n): ",
                pedidoSelecionado.getEmailRestaurante());
        String confirmacao = scanner.nextLine().trim().toLowerCase();

        if (!confirmacao.equals("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Aceita o pedido através do serviço
        boolean sucesso = contexto.servicoEntrega.aceitarPedido(entregador.getEmail(), pedidoSelecionado);

        if (sucesso) {
            System.out.println("\n✅ Pedido aceito com sucesso!");
            System.out.printf("   Taxa de Entrega: R$ %.2f\n", pedidoSelecionado.getTotal() * 0.15);
            System.out.println("   O cliente foi notificado.");
            System.out.println("\n📍 Próximos passos:");
            System.out.println("   1. Aguarde o restaurante preparar o pedido");
            System.out.println("   2. Retire o pedido no restaurante");
            System.out.println("   3. Entregue ao cliente");
        }
    }
}
