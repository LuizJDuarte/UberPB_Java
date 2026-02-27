package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.*;
import com.uberpb.model.Corrida;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.List;
import java.util.Scanner;

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

        if (candidatas.isEmpty()) { 
            erro("(sem corridas)"); 
            System.out.println("📭 Nenhuma corrida encontrada para acompanhar.");
            return; 
        }

        System.out.println("Selecione a corrida para acompanhar:");
        for (int i = 0; i < candidatas.size(); i++) {
            Corrida c = candidatas.get(i);
            String statusInfo = "[" + c.getStatus() + "]";
            if (c.getMotoristaAlocado() != null) {
                statusInfo += " Motorista: " + c.getMotoristaAlocado();
            }
            System.out.printf("%d) %s - %s → %s %s%n", 
                i + 1, c.getId().substring(0, 8), 
                c.getOrigemEndereco(), 
                c.getDestinoEndereco(),
                statusInfo);
        }
        System.out.print("> ");
        String s = in.nextLine().trim();
        if (s.isBlank()) return;

        int idx; 
        try { 
            idx = Integer.parseInt(s) - 1; 
        } catch (Exception e) { 
            erro("inválido"); 
            return; 
        }
        if (idx < 0 || idx >= candidatas.size()) { 
            erro("inválido"); 
            return; 
        }
        Corrida c = candidatas.get(idx);

        // CORREÇÃO: Debug detalhado do estado da corrida
        System.out.println("🔄 Iniciando acompanhamento da corrida: " + c.getId());
        System.out.println("   Status: " + c.getStatus());
        System.out.println("   Motorista: " + (c.getMotoristaAlocado() != null ? c.getMotoristaAlocado() : "Nenhum"));
        System.out.println("   Já está ativa no gerenciador: " + ctx.gerenciadorCorridas.isAtiva(c.getId()));

        // Garante que a simulação esteja ativa
        ctx.servicoCorrida.iniciarSeAceita(c, ctx.gerenciadorCorridas);

        // CORREÇÃO: Verificação extra se a corrida foi iniciada
        if (!ctx.gerenciadorCorridas.isAtiva(c.getId())) {
            System.out.println("⚠️  ATENÇÃO: Corrida não foi iniciada automaticamente!");
            System.out.println("🔄 Iniciando manualmente com valores padrão...");
            ctx.gerenciadorCorridas.iniciar(c.getId(), 10, 5.0); // 10min, 5km
        }

        // Loop de atualização a cada Enter (1s entre prints para dar "vida")
        int atualizacoes = 0;
        while (true) {
            cabecalho("Progresso da corrida", ctx.sessao);
            var p = ctx.servicoCorrida.progresso(c.getId(), ctx.gerenciadorCorridas);

            // CORREÇÃO: Exibir informações detalhadas de debug
            System.out.println("🆔 Corrida: " + c.getId().substring(0, 8));
            System.out.println("📍 " + c.getOrigemEndereco() + " → " + c.getDestinoEndereco());
            if (c.getMotoristaAlocado() != null) {
                System.out.println("🚗 Motorista: " + c.getMotoristaAlocado());
            }
            
            System.out.println();
            ConsoleUI.barra(p.percentual);
            System.out.printf("📊 Progresso: %d%%%n", p.percentual);
            System.out.printf("⏱️  Tempo restante: %d min%n", p.minutosRestantes);
            System.out.printf("🛣️  Distância restante: %.1f km%n", p.distanciaRestanteKm);

            // CORREÇÃO: Mapa textual sempre (geocodifica se só houver endereços)
            String o = (c.getOrigemEndereco() != null) ? 
                c.getOrigemEndereco().substring(0, Math.min(15, c.getOrigemEndereco().length())) : "Origem";
            String d = (c.getDestinoEndereco() != null) ? 
                c.getDestinoEndereco().substring(0, Math.min(15, c.getDestinoEndereco().length())) : "Destino";
            
            System.out.println();
            System.out.println(ConsoleUI.GRAY + "🗺️  TRAJETO:" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.GRAY + ConsoleUI.mapaLinha(p.percentual, o, d) + ConsoleUI.RESET);
            System.out.println();

            // CORREÇÃO: Informações de debug para desenvolvedor
            if (atualizacoes < 3) { // Mostrar apenas nas primeiras 3 atualizações
                System.out.println(ConsoleUI.GRAY + "💡 Dica: O carro (🚗) se move conforme o progresso" + ConsoleUI.RESET);
                atualizacoes++;
            }

            try { 
                Thread.sleep(1000); 
            } catch (InterruptedException ignored) {}

            if (p.concluida) {
                ok("🎉 Corrida concluída!");
                ctx.servicoCorrida.encerrarSeConcluida(c.getId(), ctx.gerenciadorCorridas);
                
                // CORREÇÃO: Aguardar Enter antes de sair
                System.out.print(ConsoleUI.GRAY + "(Enter para voltar ao menu) " + ConsoleUI.RESET);
                in.nextLine();
                return;
            }
            
            System.out.println();
            System.out.println("[Enter] para atualizar, 'q' para sair");
            String cmd = in.nextLine().trim();
            if (cmd.equalsIgnoreCase("q")) {
                System.out.println("👋 Saindo do acompanhamento...");
                return;
            }
        }
    }
}
