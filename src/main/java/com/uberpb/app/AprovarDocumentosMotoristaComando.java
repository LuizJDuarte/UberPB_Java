package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import java.util.Scanner;

public class AprovarDocumentosMotoristaComando implements Comando {

    @Override
    public String nome() {
        return "Aprovar Documentos de Motorista";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        ConsoleUI.cabecalho("Aprovar Documentos", null);
        System.out.print("Digite o email do motorista a ser aprovado: ");
        String email = entrada.nextLine();

        Usuario usuario = contexto.repositorioUsuario.buscarPorEmail(email);

        if (usuario == null) {
            ConsoleUI.erro("Usuário não encontrado.");
            return;
        }

        if (!(usuario instanceof Motorista motorista)) {
            ConsoleUI.erro("O email fornecido não pertence a um motorista.");
            return;
        }

        if (motorista.isCnhValida() && motorista.isCrlvValido()) {
            ConsoleUI.ok("Os documentos deste motorista já estavam aprovados.");
            return;
        }

        motorista.setCnhValida(true);
        motorista.setCrlvValido(true);
        contexto.repositorioUsuario.atualizar(motorista);

        ConsoleUI.ok("CNH e CRLV do motorista " + email + " foram aprovados com sucesso!");
        System.out.println("Lembre-se de ativar a conta do motorista para que ele possa começar a dirigir.");
    }
}
