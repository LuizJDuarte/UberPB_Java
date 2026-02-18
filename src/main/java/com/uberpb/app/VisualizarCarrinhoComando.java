package com.uberpb.app;

import com.uberpb.model.CarrinhoCompras;
import com.uberpb.model.ItemCarrinho;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class VisualizarCarrinhoComando implements Comando {

    @Override
    public String nome() {
        return "Visualizar Carrinho de Compras";
    }

    @Override
    public boolean visivelPara(Usuario u) {
        return u instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {

        String email = contexto.sessao.getUsuarioAtual().getEmail();
        CarrinhoCompras carrinho = contexto.servicoCarrinho.obterCarrinho(email);

        if (carrinho.isEmpty()) {
            System.out.println("\nSeu carrinho está vazio.");
            return;
        }

        while (true) {

            System.out.println("\n" + BOLD + "=== Seu Carrinho de Compras ===" + RESET);
            System.out.println("Restaurante: " + carrinho.getRestaurante().getNomeFantasia());
            System.out.println("-----------------------------------");

            int index = 1;
            for (ItemCarrinho ic : carrinho.getItens()) {
                System.out.println(index + ") " + ic.toString());
                index++;
            }

            System.out.println("-----------------------------------");
            System.out.printf("Subtotal: R$ %.2f%n", carrinho.getTotalItens());
            System.out.printf("Taxa de Entrega: R$ %.2f%n", carrinho.getRestaurante().getTaxaEntrega());
            System.out.printf(BOLD + "TOTAL: R$ %.2f%n" + RESET, carrinho.getTotalGeral());
            System.out.println("-----------------------------------");

            System.out.println("1) Remover Item");
            System.out.println("2) Finalizar Pedido");
            System.out.println("3) Limpar Carrinho");
            System.out.println("0) Voltar");
            System.out.print("> ");

            String op = entrada.nextLine();

            switch (op) {

                case "1":
                    removerItem(carrinho, entrada);
                    break;

                case "2":
                    finalizarPedido(contexto, carrinho, entrada);
                    return;

                case "3":
                    carrinho.limpar();
                    ok("Carrinho esvaziado.");
                    return;

                case "0":
                    return;

                default:
                    erro("Opção inválida.");
            }
        }
    }

    private void removerItem(CarrinhoCompras carrinho, Scanner entrada) {

    System.out.print("Digite o número do item para remover: ");

    try {
        int opcao = Integer.parseInt(entrada.nextLine());
        int idx = opcao - 1;

        if (idx >= 0 && idx < carrinho.getItens().size()) {

            carrinho.removerItemPorIndice(idx);
            ok("Item removido com sucesso!");

            if (carrinho.isEmpty()) {
                System.out.println("Carrinho agora está vazio.");
            }

        } else {
            erro("Item inválido.");
        }

    } catch (NumberFormatException e) {
        erro("Entrada inválida.");
    }
}
    private void finalizarPedido(ContextoAplicacao contexto, CarrinhoCompras carrinho, Scanner entrada) {

    System.out.println("\nEscolha a forma de pagamento:");
    System.out.println("1) Cartão de Crédito");
    System.out.println("2) Cartão de Débito");
    System.out.println("3) Pix");
    System.out.println("4) Dinheiro");
    System.out.print("> ");

    String pagamento = entrada.nextLine();
    String tipoPagamento;

    switch (pagamento) {
        case "1":
            tipoPagamento = "Cartão de Crédito";
            break;
        case "2":
            tipoPagamento = "Cartão de Débito";
            break;
        case "3":
            tipoPagamento = "Pix";
            break;
        case "4":
            tipoPagamento = "Dinheiro";
            break;
        default:
            erro("Forma de pagamento inválida.");
            return;
    }

    System.out.println("\nResumo do Pedido:");
    System.out.printf("Total a pagar: R$ %.2f%n", carrinho.getTotalGeral());
    System.out.println("Forma de pagamento: " + tipoPagamento);

    System.out.println("\nConfirmar pedido?");
    System.out.println("1) Confirmar");
    System.out.println("2) Cancelar");
    System.out.print("> ");

    String confirmacao = entrada.nextLine();

    if (confirmacao.equals("1")) {

        // CRIA O PEDIDO
        var pedido = new com.uberpb.model.Pedido(
                contexto.sessao.getUsuarioAtual().getEmail(),
                carrinho.getRestaurante().getEmail(),
                carrinho.getItens(),
                carrinho.getTotalGeral(),
                tipoPagamento
        );

        // SALVA O PEDIDO
        contexto.servicoPedido.salvarPedido(pedido);

        ok("Pedido confirmado e salvo com sucesso!");

        // LIMPA O CARRINHO
        carrinho.limpar();

    } else {
        System.out.println("Pedido cancelado.");
    }
}
}
