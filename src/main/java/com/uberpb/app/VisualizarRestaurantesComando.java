package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.Localizacao;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Restaurante;
import com.uberpb.model.ItemCardapio;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import static com.uberpb.app.ConsoleUI.*;

public class VisualizarRestaurantesComando implements Comando {

    @Override
    public String nome() {
        return "Acessar restaurantes disponíveis.";
    }

    @Override
    public boolean visivelPara(Usuario u) {
        return u instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {

        Passageiro passageiro = (Passageiro) contexto.sessao.getUsuarioAtual();

        Localizacao minhaLoc =
                contexto.servicoLocalizacao.obterLocalizacaoAtual(passageiro.getEmail());

        List<Restaurante> ativos = contexto.repositorioRestaurante.listarTodos()
                .stream()
                .filter(Restaurante::isContaAtiva)
                .sorted((r1, r2) -> {
                    double d1 = contexto.servicoLocalizacao.distanciaKm(minhaLoc, r1.getLocalizacao());
                    double d2 = contexto.servicoLocalizacao.distanciaKm(minhaLoc, r2.getLocalizacao());
                    return Double.compare(d1, d2);
                })
                .collect(Collectors.toList());

        if (ativos.isEmpty()) {
            System.out.println("\nNenhum restaurante ativo encontrado no sistema.");
            return;
        }

        System.out.println("\n" + BOLD + "== Restaurantes Disponíveis ==" + RESET);

        for (int i = 0; i < ativos.size(); i++) {
            Restaurante r = ativos.get(i);
            double dist = contexto.servicoLocalizacao.distanciaKm(minhaLoc, r.getLocalizacao());

            System.out.printf("%d) %s | Distância: %.2f km | Taxa: R$ %.2f | Tempo: %d min%n",
                    i + 1,
                    r.getNomeFantasia(),
                    dist,
                    r.getTaxaEntrega(),
                    r.getTempoEstimadoEntregaMinutos());
        }

        System.out.println("0) Voltar");
        System.out.print("\nSelecione um restaurante: ");

        try {
            int opcao = Integer.parseInt(entrada.nextLine());

            if (opcao == 0) return;

            int idx = opcao - 1;

            if (idx >= 0 && idx < ativos.size()) {
                exibirCardapio(ativos.get(idx), contexto, entrada);
            } else {
                erro("Opção inválida.");
            }

        } catch (NumberFormatException e) {
            erro("Entrada inválida.");
        }
    }

    private void exibirCardapio(Restaurante restaurante,
                                ContextoAplicacao contexto,
                                Scanner entrada) {

        while (true) {

            System.out.println("\n" + BOLD +
                    "== Cardápio de " + restaurante.getNomeFantasia() +
                    " ==" + RESET);

            List<ItemCardapio> itens = restaurante.getCardapio();

            if (itens == null || itens.isEmpty()) {
                System.out.println("Nenhum item no cardápio no momento.");
                return;
            }

            for (int i = 0; i < itens.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, itens.get(i).toString());
            }

            System.out.println("0) Voltar");
            System.out.print("\nEscolha um item para adicionar ao carrinho: ");

            try {
                int opcao = Integer.parseInt(entrada.nextLine());

                if (opcao == 0) return;

                int idx = opcao - 1;

                if (idx >= 0 && idx < itens.size()) {

                    System.out.print("Quantidade: ");
                    int qtd = Integer.parseInt(entrada.nextLine());

                    if (qtd <= 0) {
                        erro("Quantidade deve ser maior que zero.");
                        continue;
                    }

                    contexto.servicoCarrinho.adicionarAoCarrinho(
                            contexto.sessao.getUsuarioAtual().getEmail(),
                            restaurante,
                            itens.get(idx),
                            qtd
                    );

                    ok("Item adicionado ao carrinho com sucesso!");

                    // NOVA PARTE P/ IR PARA O CARRINHO)
                    System.out.println("\nDeseja ir para o carrinho?");
                    System.out.println("1) Sim");
                    System.out.println("2) Continuar no cardápio");
                    System.out.print("> ");

                    String escolha = entrada.nextLine();

                    if (escolha.equals("1")) {
                        new VisualizarCarrinhoComando().executar(contexto, entrada);
                        return;
                    }

                } else {
                    erro("Item inválido.");
                }

            } catch (NumberFormatException e) {
                erro("Entrada inválida.");
            }
        }
    }
}
