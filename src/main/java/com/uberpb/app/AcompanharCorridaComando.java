package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.ProgressoCorrida;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

public class AcompanharCorridaComando implements Comando {
    @Override public String nome() { return "Acompanhar Corrida (tempo real)"; }
    @Override public boolean visivelPara(Usuario u) { return u != null; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        List<Corrida> minhas = ctx.repositorioCorrida.buscarPorPassageiro(ctx.sessao.getUsuarioAtual().getEmail());
        if (minhas.isEmpty()) { System.out.println("(sem corridas)"); return; }

        System.out.println("Escolha a corrida:");
        for (int i = 0; i < minhas.size(); i++) {
            System.out.printf("%d) %s (%s -> %s)%n", i+1, minhas.get(i).getId(),
                    minhas.get(i).getOrigemEndereco(), minhas.get(i).getDestinoEndereco());
        }
        System.out.print("> ");
        int idx;
        try { idx = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception e) { return; }
        if (idx < 0 || idx >= minhas.size()) return;

        String id = minhas.get(idx).getId();
        ProgressoCorrida p = ctx.gerenciadorCorridas.progresso(id);
        if (p == null) { System.out.println("Corrida ainda não iniciou (aguardando aceite)."); return; }

        // Loop curto de acompanhamento
        for (int t = 0; t < 10; t++) {
            p = ctx.gerenciadorCorridas.progresso(id);
            if (p == null) { System.out.println("Corrida não encontrada."); return; }

            int perc = p.percentual();
            String barra = "[" + "#".repeat(perc/10) + " ".repeat(10 - perc/10) + "]";
            System.out.printf("%s %3d%%  %.1f/%.1f km  %d/%d min%n",
                    barra, perc, p.getKmPercorridos(), p.getDistanciaTotalKm(),
                    p.getMinutosDecorridos(), p.getMinutosEstimados());

            if (p.isConcluida()) { System.out.println("Corrida concluída (tempo/rota)."); break; }
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }
}
