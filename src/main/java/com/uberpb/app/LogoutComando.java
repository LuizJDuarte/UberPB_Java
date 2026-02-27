package com.uberpb.app;

import com.uberpb.model.Usuario;
import java.util.Scanner;

public class LogoutComando implements Comando {
    @Override
    public String nome() {
        return "Logout";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null; // Mostra se estiver logado
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        contexto.getSessao().deslogar();
        ConsoleUI.ok("Logout realizado com sucesso.");
    }
}
