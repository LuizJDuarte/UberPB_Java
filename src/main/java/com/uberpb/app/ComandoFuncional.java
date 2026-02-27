package com.uberpb.app;

import com.uberpb.model.Usuario;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ComandoFuncional implements Comando {
    private final String nome;
    private final Predicate<Usuario> visivel;
    private final BiConsumer<ContextoAplicacao, Scanner> acao;

    public ComandoFuncional(String nome,
                            Predicate<Usuario> visivel,
                            BiConsumer<ContextoAplicacao, Scanner> acao) {
        this.nome = nome;
        this.visivel = visivel;
        this.acao = acao;
    }

    @Override public String nome() { return nome; }
    @Override public boolean visivelPara(Usuario usuarioAtualOuNull) { return visivel.test(usuarioAtualOuNull); }
    @Override public void executar(ContextoAplicacao contexto, Scanner entrada) { acao.accept(contexto, entrada); }
}
