package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.Restaurante;
import com.uberpb.model.ItemCardapio;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

/**
 * Comando exclusivo para Restaurantes gerenciarem seu cardápio.
 */
public class GerenciarCardapioComando implements Comando {

    @Override
    public String nome() {
        return "Gerenciamento";
    }

    @Override
    public boolean visivelPara(Usuario u) {
        return u instanceof Restaurante;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        Restaurante restaurante = (Restaurante) contexto.sessao.getUsuarioAtual();

        System.out.println("\n" + BOLD + "=== Gerenciar Restaurante: " 
                + restaurante.getNomeFantasia() + " ===" + RESET);
        System.out.println("1) Adicionar Novo Item");
        System.out.println("2) Ver Itens Atuais");
        System.out.println("3) Alterar Taxa e Tempo de Entrega");
        System.out.println("0) Voltar");
        System.out.print("> ");

        String op = entrada.nextLine();

        switch (op) {
            case "1":
                adicionarItem(restaurante, contexto, entrada);
                break;
            case "2":
                exibirItens(restaurante);
                break;
            case "3":
                alterarEntrega(restaurante, contexto, entrada);
                break;
            case "0":
                return;
            default:
                erro("Opção inválida.");
        }
    }

    private void adicionarItem(Restaurante restaurante, ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Nome do Item: ");
        String nome = entrada.nextLine();

        System.out.print("Descrição: ");
        String descricao = entrada.nextLine();

        System.out.print("Preço (ex: 25,50): ");
        String precoStr = entrada.nextLine().replace(",", ".");
        
        try {
            double preco = Double.parseDouble(precoStr);

            if (preco <= 0) {
                erro("O preço deve ser maior que zero.");
                return;
            }
            
            ItemCardapio novoItem = new ItemCardapio(nome, descricao, preco);
            restaurante.adicionarItemCardapio(novoItem);
            
            contexto.repositorioRestaurante.salvar(restaurante);
            
            ok("Item '" + nome + "' adicionado com sucesso!");
        } catch (NumberFormatException e) {
            erro("Preço inválido. Use números e vírgula/ponto.");
        }
    }

    private void alterarEntrega(Restaurante restaurante, ContextoAplicacao contexto, Scanner entrada) {

        System.out.println("\n--- Alterar Informações de Entrega ---");
        System.out.printf("Taxa atual: R$ %.2f%n", restaurante.getTaxaEntrega());
        System.out.printf("Tempo atual: %d minutos%n", restaurante.getTempoEstimadoEntregaMinutos());

        try {
            System.out.print("Nova taxa (ex: 5.00): ");
            double novaTaxa = Double.parseDouble(entrada.nextLine().replace(",", "."));

            if (novaTaxa < 0) {
                erro("Taxa não pode ser negativa.");
                return;
            }

            System.out.print("Novo tempo estimado (minutos): ");
            int novoTempo = Integer.parseInt(entrada.nextLine());

            if (novoTempo <= 0) {
                erro("Tempo deve ser maior que zero.");
                return;
            }

            restaurante.setTaxaEntrega(novaTaxa);
            restaurante.setTempoEstimadoEntregaMinutos(novoTempo);

            contexto.repositorioRestaurante.salvar(restaurante);

            ok("Informações de entrega atualizadas com sucesso!");

        } catch (NumberFormatException e) {
            erro("Valor inválido.");
        }
    }

    private void exibirItens(Restaurante restaurante) {
        System.out.println("\n--- Itens no Cardápio ---");
        if (restaurante.getCardapio().isEmpty()) {
            System.out.println("Cardápio vazio.");
        } else {
            for (ItemCardapio item : restaurante.getCardapio()) {
                System.out.println(item.toString());
            }
        }

        System.out.printf("\nTaxa de Entrega: R$ %.2f%n", restaurante.getTaxaEntrega());
        System.out.printf("Tempo Estimado: %d minutos%n", restaurante.getTempoEstimadoEntregaMinutos());
    }
}