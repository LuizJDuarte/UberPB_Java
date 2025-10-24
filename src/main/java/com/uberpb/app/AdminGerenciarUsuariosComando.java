package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.TipoUsuario;
import java.util.Scanner;

public class AdminGerenciarUsuariosComando implements Comando {

    @Override
    public String nome() {
        return "Gerenciar Usuários";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null && usuario.getTipo() == TipoUsuario.ADMIN;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        ConsoleUI.cabecalho("Gerenciamento de Usuários", contexto.getSessao());
        // Submenu for user management options
        System.out.println("1) Listar Usuários");
        System.out.println("2) Excluir Usuário");
        System.out.println("0) Voltar");
        System.out.print("> ");
        String opcao = scanner.nextLine().trim();

        switch (opcao) {
            case "1":
                listarUsuarios(contexto);
                break;
            case "2":
                excluirUsuario(contexto, scanner);
                break;
            case "0":
                return;
            default:
                ConsoleUI.erro("Opção inválida.");
        }
    }

    private void listarUsuarios(ContextoAplicacao contexto) {
        contexto.getRepositorioUsuario().buscarTodos().forEach(System.out::println);
    }

    private void excluirUsuario(ContextoAplicacao contexto, Scanner scanner) {
        System.out.print("Email do usuário a ser excluído: ");
        String email = scanner.nextLine();
        Usuario usuario = contexto.getRepositorioUsuario().buscarPorEmail(email);
        if (usuario != null) {
            contexto.getRepositorioUsuario().remover(email);
            ConsoleUI.ok("Usuário removido com sucesso.");
        } else {
            ConsoleUI.erro("Usuário não encontrado.");
        }
    }
}
