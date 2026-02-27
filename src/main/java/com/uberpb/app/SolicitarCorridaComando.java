package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.service.EstimativaCorrida;

import java.util.Scanner;

public class SolicitarCorridaComando implements Comando {
    @Override public String nome() { return "Solicitar Corrida (informar endereços)"; }
    @Override public boolean visivelPara(Usuario u) { return u instanceof Passageiro; }

    @Override
    public void executar(ContextoAplicacao ctx, Scanner in) {
        System.out.println("Informe os endereços. Ex.: \"Av. Epitácio Pessoa, 1000 - Tambaú\".");
        System.out.print("Endereço de ORIGEM: ");
        String origem = in.nextLine();
        System.out.print("Endereço de DESTINO: ");
        String destino = in.nextLine();

        CategoriaVeiculo[] cats = CategoriaVeiculo.values();
        for (int i = 0; i < cats.length; i++) System.out.printf("%d) %s%n", i+1, cats[i].name());
        System.out.print("Categoria > ");
        int idx = 0;
        try { idx = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception ignored) {}
        if (idx < 0 || idx >= cats.length) idx = 0;
        CategoriaVeiculo categoria = cats[idx];

        MetodoPagamento[] mps = MetodoPagamento.values();
        for (int i = 0; i < mps.length; i++) System.out.printf("%d) %s%n", i+1, mps[i].name());
        System.out.print("Pagamento > ");
        int ip = 0;
        try { ip = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception ignored) {}
        if (ip < 0 || ip >= mps.length) ip = 0;
        MetodoPagamento mp = mps[ip];

        EstimativaCorrida est = ctx.servicoCorrida.estimarPorEnderecos(origem, destino, categoria);
        System.out.printf("Estimativa: %.1f km • %d min • R$ %.2f%n",
                est.getDistanciaKm(), est.getMinutos(), est.getPreco());

        System.out.print("Confirmar? (s/N): ");
        if (!"s".equalsIgnoreCase(in.nextLine().trim())) { System.out.println("Cancelado."); return; }

        var corrida = ctx.servicoCorrida.solicitarCorrida(
                ctx.sessao.getUsuarioAtual().getEmail(), origem, destino, categoria, mp);

        int notificadas = ctx.servicoOferta.criarOfertasParaCorrida(corrida);
        System.out.println("Corrida criada! ID: " + corrida.getId() + " | ofertas enviadas: " + notificadas);
    }
}
