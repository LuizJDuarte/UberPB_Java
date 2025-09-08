package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;

import java.util.Scanner;

public class SolicitarCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Solicitar Corrida (informar endereços)";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("Informe os endereços. Ex.: \"Av. Epitácio Pessoa, 1000 - Tambau\".");
        System.out.print("Endereço de ORIGEM: ");
        String origem = entrada.nextLine();
        System.out.print("Endereço de DESTINO: ");
        String destino = entrada.nextLine();

        Corrida corrida = contexto.servicoCorrida
                .solicitarCorrida(contexto.sessao.getUsuarioAtual().getEmail(), origem, destino);

        System.out.println("Corrida criada! ID: " + corrida.getId());
        System.out.println("(Dados salvos em data/corridas.txt)");
    }
}
