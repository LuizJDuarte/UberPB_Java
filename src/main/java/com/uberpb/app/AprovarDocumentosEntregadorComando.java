package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Usuario;

import java.util.Scanner;

public class AprovarDocumentosEntregadorComando implements Comando {

    @Override
    public String nome() {
        return "Aprovar Documentos de Entregador";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null && usuario instanceof com.uberpb.model.Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        ConsoleUI.cabecalho("Aprovar Documentos (Entregador)", null);
        System.out.print("Digite o email do entregador a ser aprovado: ");
        String email = entrada.nextLine();

        Usuario usuario = contexto.repositorioUsuario.buscarPorEmail(email);

        if (usuario == null) {
            ConsoleUI.erro("Usuário não encontrado.");
            return;
        }

        if (!(usuario instanceof Entregador entregador)) {
            ConsoleUI.erro("O email fornecido não pertence a um entregador.");
            return;
        }

        if (entregador.isCnhValida() && entregador.isDocIdentidadeValido()) {
            ConsoleUI.ok("Os documentos deste entregador já estavam aprovados.");
            return;
        }

        entregador.setCnhValida(true);
        entregador.setDocIdentidadeValido(true);
        contexto.repositorioUsuario.atualizar(entregador);

        ConsoleUI.ok("Documentos do entregador " + email + " aprovados com sucesso!");
        System.out.println("Lembre-se de ativar a conta do entregador para que ele possa começar a operar.");
    }
}
