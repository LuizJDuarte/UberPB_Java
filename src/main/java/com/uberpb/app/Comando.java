package com.uberpb.app;

import com.uberpb.model.Usuario;
import java.util.Scanner;

/**
 * Cada opção de menu vira um "Comando" independente e testável.
 * A tela principal apenas lista e despacha para o comando selecionado.
 */
public interface Comando {

    /** Nome exibido no menu. */
    String nome();

    /**
     * Indica se o comando deve aparecer no menu, dado o usuário atual (ou null).
     * Ex.: comandos de cadastro podem aparecer apenas para não logados.
     */
    boolean visivelPara(Usuario usuarioAtualOuNull);

    /** Executa a ação do comando. */
    void executar(ContextoAplicacao contexto, Scanner entrada);
}
