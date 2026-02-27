package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class AdminComando implements Comando {
    @Override public String nome() { return "Admin: Gerenciar (listar/remover)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        System.out.println("1) Listar usuários");
        System.out.println("2) Remover usuário");
        System.out.println("3) Remover corrida");
        System.out.print("> ");
        String op = in.nextLine().trim();

        switch (op) {
            case "1" -> {
                List<Usuario> usuarios = ctx.repositorioUsuario.buscarTodos();
                if (usuarios.isEmpty()) { System.out.println("(vazio)"); return; }
                for (Usuario u : usuarios) {
                    // Mostra email + hash da senha (não senha clara!)
                    System.out.println(u.getClass().getSimpleName().toUpperCase() + " | " + u.getEmail() + " | hash=" + u.getSenhaHash());
                }
            }
            case "2" -> {
                System.out.print("Email: ");
                String email = in.nextLine().trim();
                ctx.servicoAdmin.removerUsuario(email, ctx.sessao.getUsuarioAtual());
                ok("Usuário removido.");
            }
            case "3" -> {
                System.out.print("ID da corrida: ");
                String id = in.nextLine().trim();
                ctx.servicoAdmin.removerCorrida(id, ctx.sessao.getUsuarioAtual());
                ok("Corrida removida.");
            }
            default -> erro("Opção inválida.");
        }
    }
}
