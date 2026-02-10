package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.Entregador;

import java.util.Scanner;

public class CadastrarEntregadorComando implements Comando {

    @Override
    public String nome() {
        return "Cadastrar Entregador";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull == null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Email: ");
        String email = entrada.nextLine();

        System.out.print("Senha: ");
        String senha = entrada.nextLine();

        System.out.print("Número CNH (ou deixe vazio): ");
        String cnh = entrada.nextLine();

        System.out.print("Número CPF: ");
        String cpf = entrada.nextLine();

        Entregador e = contexto.servicoCadastro.cadastrarEntregador(email, senha, cnh, cpf);
        System.out.println("OK! Entregador cadastrado: " + e.getEmail());
        System.out.println("Aguardando validação de documentos pelo admin.");
    }
}
