package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.EstimativaCorrida;

import java.util.Scanner;

public class SolicitarCorridaComando implements Comando {

    @Override public String nome() { return "Solicitar Corrida (informar endereços)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("Informe os endereços. Ex.: \"Av. Epitácio Pessoa, 1000 - Tambaú\".");
        System.out.print("Endereço de ORIGEM: ");
        String origem = entrada.nextLine();
        System.out.print("Endereço de DESTINO: ");
        String destino = entrada.nextLine();

        // Categoria (RF06)
        CategoriaVeiculo[] cats = CategoriaVeiculo.values();
        System.out.println("Escolha a categoria (digite o número):");
        for (int i = 0; i < cats.length; i++) System.out.printf("%d) %s%n", i+1, cats[i].name());
        System.out.print("> ");
        String s = entrada.nextLine().trim();
        int idx = 0;
        try { idx = Integer.parseInt(s) - 1; } catch (Exception e) { idx = 0; }
        if (idx < 0 || idx >= cats.length) idx = 0;
        CategoriaVeiculo categoriaEscolhida = cats[idx];

        // Estimativa (RF05)
        EstimativaCorrida est = contexto.servicoCorrida.estimarPorEnderecos(origem, destino, categoriaEscolhida);
        System.out.printf("Estimativa: %.1f km • %d min • R$ %.2f%n",
                est.getDistanciaKm(), est.getMinutos(), est.getPreco());

        System.out.print("Confirmar solicitação? (s/N): ");
        String conf = entrada.nextLine().trim();
        if (!conf.equalsIgnoreCase("s")) {
            System.out.println("Solicitação cancelada.");
            return;
        }

        // Solicitação (RF04) com categoria (RF06) + notificação (RF07)
        Corrida corrida = contexto.servicoCorrida
                .solicitarCorrida(contexto.sessao.getUsuarioAtual().getEmail(), origem, destino, categoriaEscolhida);

        System.out.println("Corrida criada! ID: " + corrida.getId());
        int notificadas = 0;
        try { notificadas = contexto.servicoOferta.criarOfertasParaCorrida(corrida); }
        catch (Exception e) { System.err.println("Erro ao notificar motoristas: " + e.getMessage()); }
        System.out.println("Ofertas enviadas para " + notificadas + " motoristas da categoria " +
                (categoriaEscolhida != null ? categoriaEscolhida.name() : "(todas)"));
        System.out.println("(Dados salvos em data/corridas.txt e data/ofertas.txt)");
    }
}
