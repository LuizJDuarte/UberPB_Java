package com.uberpb.app;

import com.uberpb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Comando para visualizar notificações do usuário (RF22)
 */
public class VisualizarNotificacoesComando implements Comando {

    @Override
    public String nome() {
        return "Visualizar Notificações";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        // Disponível apenas para usuários logados
        return usuarioAtualOuNull != null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        String email = contexto.sessao.getUsuarioAtual().getEmail();

        // Buscar notificações não lidas
        List<Notificacao> naoLidas = contexto.servicoNotificacao.buscarNotificacoesNaoLidas(email);

        // Buscar todas as notificações
        List<Notificacao> todas = contexto.servicoNotificacao.buscarNotificacoes(email);

        System.out.println("\n========== MINHAS NOTIFICAÇÕES ==========");
        System.out.println("Total: " + todas.size() + " | Não lidas: " + naoLidas.size());
        System.out.println("=========================================\n");

        if (todas.isEmpty()) {
            System.out.println("Você não tem notificações.");
            return;
        }

        // Mostrar não lidas primeiro
        if (!naoLidas.isEmpty()) {
            System.out.println("📬 NOTIFICAÇÕES NÃO LIDAS:");
            for (int i = 0; i < naoLidas.size(); i++) {
                System.out.println((i + 1) + ". " + naoLidas.get(i));
            }
            System.out.println();
        }

        // Mostrar todas as outras
        List<Notificacao> lidas = new ArrayList<>(todas);
        lidas.removeAll(naoLidas);

        if (!lidas.isEmpty()) {
            System.out.println("✅ NOTIFICAÇÕES LIDAS:");
            for (Notificacao n : lidas) {
                System.out.println("   " + n);
            }
        }

        // Opção de marcar todas como lidas
        if (!naoLidas.isEmpty()) {
            System.out.print("\nMarcar todas como lidas? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();

            if (resposta.equals("s")) {
                for (Notificacao n : naoLidas) {
                    contexto.servicoNotificacao.marcarComoLida(n.getId());
                }
                System.out.println("✓ Todas as notificações foram marcadas como lidas.");
            }
        }
    }
}
