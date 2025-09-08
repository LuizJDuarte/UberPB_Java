package com.uberpb.app;

import com.uberpb.model.Usuario;

import java.util.Scanner;

/**
 * Comando para cadastrar um Motorista.
 * Ajustado para a assinatura atual de ServicoCadastro: (email, senha).
 */
public class CadastrarMotoristaComando implements Comando {

    @Override
    public String nome() {
        return "Cadastrar Motorista";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        // Visível apenas para não logados (ajuste conforme sua política).
        return usuarioAtualOuNull == null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Email: ");
        String email = entrada.nextLine();

        System.out.print("Senha: ");
        String senha = entrada.nextLine();

        // Assinatura vigente no seu ServicoCadastro
        contexto.servicoCadastro.cadastrarMotorista(email, senha);

        System.out.println("OK! Motorista cadastrado.");
        System.out.println("(Observação: dados de veículo/documentos podem ser coletados em outro fluxo, conforme o seu service atual.)");
    }
}
