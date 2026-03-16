package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import java.util.Scanner;

public class FicarOnlineOfflineComando implements Comando {

    @Override
    public String nome() {
        // O nome do comando mudará dinamicamente
        return "Ficar Online/Offline";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        if (usuario instanceof Motorista motorista) {
            // Só é visível para motoristas totalmente aprovados e com conta ativa
            return motorista.getVeiculo() != null &&
                   motorista.isCnhValida() &&
                   motorista.isCrlvValido() &&
                   motorista.isContaAtiva();
        }
        return false;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        Motorista motorista = (Motorista) contexto.getSessao().getUsuarioAtual();
        if (motorista == null) {
            ConsoleUI.erro("Nenhum motorista logado.");
            return;
        }

        // Inverte o estado atual
        boolean novoStatus = !motorista.isDisponivel();
        motorista.setDisponivel(novoStatus);
        contexto.getRepositorioUsuario().atualizar(motorista);

        if (novoStatus) {
            ConsoleUI.ok("Você está ONLINE. Aguardando novas corridas.");
        } else {
            ConsoleUI.ok("Você está OFFLINE. Você não receberá novas ofertas de corrida.");
        }
    }

    // Sobrescreve o nome para ser dinâmico no menu
    @Override
    public String nomeParaExibicao(Usuario usuario) {
        if (usuario instanceof Motorista motorista && motorista.isDisponivel()) {
            return "Ficar Offline";
        }
        return "Ficar Online";
    }
}
