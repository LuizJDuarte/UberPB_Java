package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class LimparAvaliacoesComando implements Comando {
    @Override
    public String nome() {
        return "Limpar Avaliações";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        contexto.repositorioAvaliacao.limpar();
        ok("Base de avaliações limpa com sucesso.");
    }
}
