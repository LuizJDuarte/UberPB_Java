package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.TipoUsuario;
import java.util.Scanner;

public class AdminModerarAvaliacoesComando implements Comando {

    @Override
    public String nome() {
        return "Moderar Avaliações";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null && usuario.getTipo() == TipoUsuario.ADMIN;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        ConsoleUI.cabecalho("Moderação de Avaliações", contexto.getSessao());
        // Submenu for rating management
        System.out.println("1) Visualizar Avaliações");
        System.out.println("2) Remover Avaliação");
        System.out.println("0) Voltar");
        System.out.print("> ");
        String opcao = scanner.nextLine().trim();

        switch (opcao) {
            case "1":
                listarAvaliacoes(contexto);
                break;
            case "2":
                removerAvaliacao(contexto, scanner);
                break;
            case "0":
                return;
            default:
                ConsoleUI.erro("Opção inválida.");
        }
    }

    private void listarAvaliacoes(ContextoAplicacao contexto) {
        // contexto.getRepositorioAvaliacao().buscarTodos().forEach(System.out::println);
        System.out.println("Funcionalidade ainda não implementada.");
    }

    private void removerAvaliacao(ContextoAplicacao contexto, Scanner scanner) {
        System.out.print("ID da avaliação a ser removida: ");
        long id = Long.parseLong(scanner.nextLine());
        // Assuming you have a way to get the specific evaluation to remove
        // For now, let's assume you'll implement a findById in the repository
        ConsoleUI.erro("Funcionalidade ainda não implementada.");
    }
}
