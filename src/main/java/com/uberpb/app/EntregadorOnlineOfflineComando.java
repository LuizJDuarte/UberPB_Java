package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Usuario;

import java.util.Scanner;

/**
 * Comando para entregador ficar online/offline (RF22)
 * Similar ao FicarOnlineOfflineComando dos motoristas
 */
public class EntregadorOnlineOfflineComando implements Comando {

    @Override
    public String nome() {
        return "[Entregador] Online/Offline";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        // Disponível apenas para entregadores logados
        return usuarioAtualOuNull instanceof Entregador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        Usuario usuario = contexto.sessao.getUsuarioAtual();

        if (!(usuario instanceof Entregador entregador)) {
            System.out.println("⚠️ Este comando é apenas para entregadores.");
            return;
        }

        if (!entregador.isContaAtiva()) {
            System.out.println("⚠️ Sua conta ainda não foi ativada pelo administrador.");
            System.out.println("   Aguarde a aprovação dos seus documentos.");
            return;
        }

        boolean statusAtual = entregador.isDisponivel();
        String statusTexto = statusAtual ? "ONLINE" : "OFFLINE";

        System.out.println("\n========== STATUS DE DISPONIBILIDADE ==========");
        System.out.println("Status atual: " + statusTexto);
        System.out.println("===============================================\n");

        System.out.print("Deseja mudar o status? (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();

        if (!resposta.equals("s")) {
            System.out.println("Status mantido: " + statusTexto);
            return;
        }

        boolean novoStatus = !statusAtual;
        entregador.setDisponivel(novoStatus);
        contexto.repositorioUsuario.atualizar(entregador);

        String novoStatusTexto = novoStatus ? "ONLINE ✅" : "OFFLINE ⏸️";
        System.out.println("\n✓ Status alterado para: " + novoStatusTexto);

        if (novoStatus) {
            System.out.println("Você está ONLINE e pode receber pedidos de entrega!");
        } else {
            System.out.println("Você está OFFLINE e não receberá novos pedidos.");
        }
    }
}
