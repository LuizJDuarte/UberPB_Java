package com.uberpb.app;

import com.uberpb.model.Pedido;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.List;
import java.util.Scanner;

public class VisualizarPedidosComando implements Comando {
    @Override public String nome() { return "Meus Pedidos Anteriores"; }
    @Override public boolean visivelPara(Usuario u) { return u instanceof Passageiro; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        System.out.println("\n=== Meus Pedidos ===");
        List<Pedido> pedidos = ctx.servicoPedido.buscarPorCliente(ctx.sessao.getUsuarioAtual().getEmail());

        if (pedidos.isEmpty()) {
            System.out.println("(Nenhum pedido encontrado)");
            return;
        }

        for (Pedido p : pedidos) {
            System.out.printf("Restaurante: %s | Total: R$ %.2f | Status: %s%n",
                    p.getEmailRestaurante(), p.getTotal(), p.getStatus());
        }
    }
}