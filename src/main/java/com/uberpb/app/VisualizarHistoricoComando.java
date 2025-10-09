package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

public class VisualizarHistoricoComando implements Comando {
    @Override public String nome() { return "Histórico de Corridas (filtrar por categoria)"; }
    @Override public boolean visivelPara(Usuario u) { return u != null; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        System.out.println("Filtrar por categoria? (vazio = todas)");
        CategoriaVeiculo[] cats = CategoriaVeiculo.values();
        for (int i = 0; i < cats.length; i++) System.out.printf("%d) %s%n", i+1, cats[i].name());
        System.out.print("> ");
        String s = in.nextLine().trim();
        CategoriaVeiculo filtro = null;
        if (!s.isBlank()) {
            try {
                int idx = Integer.parseInt(s) - 1;
                if (idx >= 0 && idx < cats.length) filtro = cats[idx];
            } catch (Exception ignored) {}
        }
        final CategoriaVeiculo f = filtro; // <- final p/ lambda

        List<Corrida> todas = ctx.repositorioCorrida.buscarPorPassageiro(ctx.sessao.getUsuarioAtual().getEmail());
        if (f != null) todas = todas.stream().filter(c -> f.equals(c.getCategoriaEscolhida())).toList();

        if (todas.isEmpty()) { System.out.println("(vazio)"); return; }
        for (Corrida c : todas) {
            System.out.printf("%s | %s -> %s | cat=%s | status=%s%n",
                    c.getId(), c.getOrigemEndereco(), c.getDestinoEndereco(),
                    c.getCategoriaEscolhida(), c.getStatus());
        }
    }
}
