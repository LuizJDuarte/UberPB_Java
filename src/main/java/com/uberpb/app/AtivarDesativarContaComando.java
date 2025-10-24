package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.Scanner;

public class AtivarDesativarContaComando implements Comando {

    @Override
    public String nome() {
        return "Ativar/Desativar Conta de Usuário";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        ConsoleUI.cabecalho("Ativar/Desativar Conta", null);
        System.out.print("Digite o email do usuário (motorista ou passageiro): ");
        String email = entrada.nextLine();

        Usuario usuario = contexto.repositorioUsuario.buscarPorEmail(email);

        if (usuario == null) {
            ConsoleUI.erro("Usuário não encontrado.");
            return;
        }

        if (usuario instanceof Administrador) {
            ConsoleUI.erro("Não é possível alterar o status de um administrador.");
            return;
        }

        boolean novoStatus;
        if (usuario instanceof Motorista motorista) {
            novoStatus = !motorista.isContaAtiva();
            motorista.setContaAtiva(novoStatus);
        } else if (usuario instanceof Passageiro passageiro) {
            novoStatus = !passageiro.isContaAtiva();
            passageiro.setContaAtiva(novoStatus);
        } else {
            ConsoleUI.erro("Tipo de usuário desconhecido.");
            return;
        }

        contexto.repositorioUsuario.atualizar(usuario);

        String statusMsg = novoStatus ? "ATIVA" : "DESATIVADA";
        ConsoleUI.ok("A conta do usuário " + email + " foi definida como " + statusMsg + ".");
    }
}
