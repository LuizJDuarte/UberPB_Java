package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.Restaurante;

import java.util.Scanner;

public class CadastrarRestauranteComando implements Comando {

    @Override
    public String nome() {
        return "Cadastrar Restaurante";
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

        System.out.print("Nome fantasia do restaurante: ");
        String nome = entrada.nextLine();

        System.out.print("CNPJ (ou deixe vazio): ");
        String cnpj = entrada.nextLine();

        Restaurante r = contexto.servicoCadastro.cadastrarRestaurante(email, senha, nome, cnpj);
        System.out.println("OK! Restaurante cadastrado: " + r.getEmail());
        System.out.println("Aguardando ativação/validação pelo admin.");
    }
}
