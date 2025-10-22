package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.GerenciadorCorridasAtivas;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class AcompanharCorridaComando implements Comando {

    @Override public String nome() { return "Acompanhar Corrida (tempo real)"; }

    @Override
    public boolean visivelPara(Usuario u) { return u instanceof Passageiro || u instanceof Motorista; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        String email = ctx.sessao.getUsuarioAtual().getEmail();
        List<Corrida> candidatas =
                (ctx.sessao.getUsuarioAtual() instanceof Passageiro)
                        ? ctx.repositorioCorrida.buscarPorPassageiro(email)
                        : ctx.repositorioCorrida.buscarTodas(); // motorista vê as que aceitou

        if (candidatas.isEmpty()) { erro("(sem corridas)"); return; }

        System.out.println("Selecione a corrida para acompanhar:");
        for (int i = 0; i < candidatas.size(); i++)
            System.out.printf("%d) %s%n", i + 1, candidatas.get(i).getId());
        System.out.print("> ");
        String s = in.nextLine().trim();
        if (s.isBlank()) return;

        int idx; try { idx = Integer.parseInt(s) - 1; } catch (Exception e) { erro("inválido"); return; }
        if (idx < 0 || idx >= candidatas.size()) { erro("inválido"); return; }
        Corrida c = candidatas.get(idx);

        // Garante que a simulação esteja ativa
        ctx.servicoCorrida.iniciarSeAceita(c, ctx.gerenciadorCorridas);

        // Loop de atualização a cada Enter (1s entre prints para dar “vida”)
        while (true) {
            cabecalho("Progresso da corrida", ctx.sessao);
            var p = ctx.servicoCorrida.progresso(c.getId(), ctx.gerenciadorCorridas);

            ConsoleUI.barra(p.percentual);
            System.out.printf("Restante: %d min • %.1f km%n", p.minutosRestantes, p.distanciaRestanteKm);

            // mapa textual sempre (geocodifica se só houver endereços)
            String o = (c.getOrigemEndereco() != null) ? c.getOrigemEndereco() : "Origem";
            String d = (c.getDestinoEndereco() != null) ? c.getDestinoEndereco() : "Destino";
            System.out.println(ConsoleUI.GRAY + ConsoleUI.mapaLinha(p.percentual, "Origem", "Destino") + ConsoleUI.RESET);

            System.out.println();
            System.out.println("[Enter] para atualizar, 'q' para sair");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            if (p.concluida) {
                ok("Corrida concluída!");
                ctx.servicoCorrida.encerrarSeConcluida(c.getId(), ctx.gerenciadorCorridas);
                return;
            }
            String cmd = in.nextLine().trim();
            if (cmd.equalsIgnoreCase("q")) return;
        }
    }
}
