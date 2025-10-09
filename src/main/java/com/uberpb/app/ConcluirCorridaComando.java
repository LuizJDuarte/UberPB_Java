package com.uberpb.app;

import com.uberpb.exceptions.CorridaAindaEmAndamentoException;
import com.uberpb.model.Corrida;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class ConcluirCorridaComando implements Comando {
    @Override public String nome() { return "Concluir Corrida"; }
    @Override public boolean visivelPara(Usuario u) { return u != null; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        String email = ctx.sessao.getUsuarioAtual().getEmail();
        List<Corrida> minhas = ctx.repositorioCorrida.buscarPorPassageiro(email);
        if (minhas.isEmpty()) { erro("Sem corridas."); return; }

        System.out.println("Selecione a corrida:");
        for (int i = 0; i < minhas.size(); i++) {
            var c = minhas.get(i);
            System.out.printf("%d) %s (%s -> %s)%n", i+1, c.getId(), c.getOrigemEndereco(), c.getDestinoEndereco());
        }
        System.out.print("> ");
        int idx;
        try { idx = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception e) { return; }
        if (idx < 0 || idx >= minhas.size()) return;

        var corrida = minhas.get(idx);

        try {
            ctx.servicoCorrida.concluirCorrida(corrida.getId(), email, ctx.gerenciadorCorridas);
            ok("Corrida concluída.");
        } catch (CorridaAindaEmAndamentoException e) {
            erro(e.getMessage());
        }
    }
}
