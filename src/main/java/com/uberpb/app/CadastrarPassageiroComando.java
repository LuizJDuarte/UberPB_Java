package com.uberpb.app;

import com.uberpb.model.Usuario;

import java.util.Scanner;

/**
 * Comando para cadastrar um Passageiro.
 * Reutiliza ServicoCadastro existente.
 */
public class CadastrarPassageiroComando implements Comando {

    @Override
    public String nome() {
        return "Cadastrar Passageiro";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        // Deixe visível quando NÃO há ninguém logado.
        return usuarioAtualOuNull == null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Email: ");
        String email = entrada.nextLine();

        System.out.print("Senha: ");
        String senha = entrada.nextLine();

        System.out.print("Seu endereço (ex: Bairro Liberdade): ");
    String endereco = entrada.nextLine();

    var p = contexto.servicoCadastro.cadastrarPassageiro(email, senha);

    com.uberpb.model.Localizacao loc = contexto.servicoLocalizacao.geocodificar(endereco);
    p.setLocalizacao(loc);

        System.out.println("OK! Passageiro cadastrado.");
    }
}
