package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class LimparCorridasComando implements Comando {
    @Override
    public String nome() {
        return "Limpar Corridas";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        contexto.repositorioCorrida.limpar();
        contexto.repositorioOferta.limpar();
        ok("Base de corridas e ofertas limpa com sucesso.");
    }
}
