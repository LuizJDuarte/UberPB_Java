package com.uberpb.app;

import com.uberpb.model.Pedido;
import com.uberpb.model.Restaurante;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

public class GerenciarPedidosComando implements Comando {

    @Override
    public String nome() {
        return "Gerenciar Pedidos";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario instanceof Restaurante;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {

        Restaurante restaurante = (Restaurante) contexto.sessao.getUsuarioAtual();

        List<Pedido> pedidos = contexto.servicoPedido
                .buscarPorRestaurante(restaurante.getEmail());

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }

        System.out.println("\nPedidos do restaurante:");

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);

            System.out.println(
                    (i + 1) +
                            " - Cliente: " + p.getEmailCliente() +
                            " | Total: R$ " + p.getTotal() +
                            " | Status: " + p.getStatus());
        }

        System.out.print("\nEscolha o pedido: ");
        int opcao = Integer.parseInt(scanner.nextLine());

        Pedido pedido = pedidos.get(opcao - 1);

        System.out.println("\nGerenciar pedido:");
        System.out.println("1) Confirmar pedido");
        System.out.println("2) Marcar como EM PREPARO");
        System.out.println("3) Marcar como SAIU PARA ENTREGA");
        System.out.println("4) Recusar pedido");
        System.out.print("> ");

        int escolha = Integer.parseInt(scanner.nextLine());

        switch (escolha) {

            case 1:

                if (!pedido.getStatus().equals("CRIADO")) {
                    System.out.println("Este pedido já foi processado.");
                    return;
                }

                pedido.setStatus("CONFIRMADO");
                contexto.servicoPedido.atualizarPedido(pedido);

                contexto.servicoEntrega.buscarEntregadorParaPedido(pedido);

                contexto.servicoNotificacao.notificarCliente(
                        pedido.getEmailCliente(),
                        "Seu pedido foi confirmado pelo restaurante.");

                System.out.println("Pedido confirmado.");
                break;

            case 2:

                String status = pedido.getStatus();

                if (!status.equals("CONFIRMADO") && !status.equals("ACEITO")) {
                    System.out.println("O pedido precisa estar CONFIRMADO ou ACEITO primeiro.");
                    return;
                }

                pedido.setStatus("EM_PREPARO");
                contexto.servicoPedido.atualizarPedido(pedido);

                contexto.servicoNotificacao.notificarCliente(
                        pedido.getEmailCliente(),
                        "Seu pedido está sendo preparado.");

                // Notifica entregador se já existir um alocado para este pedido
                if (pedido.getEntregadorAlocado() != null) {

                    contexto.servicoNotificacao.enviarNotificacao(
                            pedido.getEntregadorAlocado(),
                            com.uberpb.model.TipoNotificacao.PEDIDO_EM_ROTA,
                            "O pedido do cliente " + pedido.getEmailCliente() + " agora está em preparo.");
                }

                System.out.println("Pedido agora está EM PREPARO.");
                break;

            case 3:

                if (!pedido.getStatus().equals("EM_PREPARO")) {
                    System.out.println("O pedido precisa estar EM PREPARO primeiro.");
                    return;
                }

                pedido.setStatus("SAIU_PARA_ENTREGA");
                contexto.servicoPedido.atualizarPedido(pedido);

                contexto.servicoNotificacao.notificarCliente(
                        pedido.getEmailCliente(),
                        "Seu pedido saiu para entrega.");

                System.out.println("Pedido saiu para entrega.");
                break;

            case 4:

                if (!pedido.getStatus().equals("CRIADO")) {
                    System.out.println("Não é possível recusar este pedido agora.");
                    return;
                }

                pedido.setStatus("RECUSADO");
                contexto.servicoPedido.atualizarPedido(pedido);

                contexto.servicoNotificacao.notificarCliente(
                        pedido.getEmailCliente(),
                        "Seu pedido foi recusado pelo restaurante.");

                System.out.println("Pedido recusado.");
                break;

            default:
                System.out.println("Opção inválida.");
        }
    }
}