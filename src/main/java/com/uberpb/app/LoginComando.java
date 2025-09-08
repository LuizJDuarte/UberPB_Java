package com.uberpb.app;

import com.uberpb.model.Usuario;

import java.util.Scanner;

/**
 * Comando de Login. Reutiliza ServicoAutenticacao existente.
 * Após autenticar, guarda o Usuario na Sessao.
 */
public class LoginComando implements Comando {

    @Override
    public String nome() {
        return "Login";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        // Só aparece quando ninguém está logado.
        return usuarioAtualOuNull == null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Email: ");
        String email = entrada.nextLine();

        System.out.print("Senha: ");
        String senha = entrada.nextLine();

        // Seu serviço deve devolver um Usuario (Passageiro/Motorista):
        Usuario usuario = contexto.servicoAutenticacao.autenticar(email, senha);

        contexto.sessao.logar(usuario);
        System.out.println("Bem-vindo, " + usuario.getEmail() + "!");
    }
}
