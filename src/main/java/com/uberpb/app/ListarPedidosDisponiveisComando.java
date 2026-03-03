package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Pedido;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * RF24: Comando para entregador visualizar pedidos disponíveis para aceitar
 */
public class ListarPedidosDisponiveisComando implements Comando {

    @Override
    public String nome() {
        return "[Entregador] Pedidos Disponíveis";
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
            System.out.println("⚠️ Você precisa estar online para visualizar pedidos.");
            System.out.println("   Use o comando 'Online/Offline' para ficar disponível.");
            return;
        }

        System.out.println("\n========== PEDIDOS DISPONÍVEIS ==========");

        List<Pedido> pedidosDisponiveis = contexto.servicoPedido
                .buscarPedidosDisponiveisParaEntregador(entregador.getEmail());

        if (pedidosDisponiveis.isEmpty()) {
            System.out.println("(Nenhum pedido disponível no momento)");
            System.out.println("=========================================\n");
            return;
        }

        int contador = 1;
        for (Pedido pedido : pedidosDisponiveis) {
            System.out.printf("\n[%d] Pedido:\n", contador++);
            System.out.printf("    Restaurante: %s\n", pedido.getEmailRestaurante());
            System.out.printf("    Cliente: %s\n", pedido.getEmailCliente());
            System.out.printf("    Total: R$ %.2f\n", pedido.getTotal());
            System.out.printf("    Taxa de Entrega (estimada): R$ %.2f\n", pedido.getTotal() * 0.15);
            System.out.printf("    Status: %s\n", pedido.getStatus());
            System.out.printf("    Pagamento: %s\n", pedido.getFormaPagamento());
        }

        System.out.println("\n=========================================");
        System.out.printf("Total de pedidos disponíveis: %d\n", pedidosDisponiveis.size());
        System.out.println("\nUse os comandos 'Aceitar Pedido' ou 'Recusar Pedido' para responder.");
    }
}
