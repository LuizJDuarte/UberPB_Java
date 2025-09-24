package com.uberpb.app; 

import com.uberpb.model.*;
import com.uberpb.service.ServicoLocalizacao;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class AcompanharCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Acompanhar Corrida em Andamento";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String emailPassageiro = contexto.sessao.getUsuarioAtual().getEmail();
        
        // Buscar corridas em andamento do passageiro
        List<Corrida> corridas = contexto.repositorioCorrida.buscarPorPassageiro(emailPassageiro);
        List<Corrida> emAndamento = corridas.stream()
                .filter(c -> c.getStatus() == CorridaStatus.EM_ANDAMENTO)
                .toList();

        if (emAndamento.isEmpty()) {
            erro("Nenhuma corrida em andamento no momento.");
            return;
        }

        System.out.println("=== ACOMPANHAMENTO DE CORRIDA ===");
        for (int i = 0; i < emAndamento.size(); i++) {
            Corrida c = emAndamento.get(i);
            System.out.printf("%d) Corrida %s - Motorista: %s%n", 
                i + 1, c.getId().substring(0, 8), 
                c.getMotoristaAlocado() != null ? c.getMotoristaAlocado() : "Não atribuído");
        }

        System.out.print("Selecione a corrida para acompanhar: ");
        try {
            int escolha = Integer.parseInt(entrada.nextLine().trim());
            if (escolha < 1 || escolha > emAndamento.size()) {
                erro("Opção inválida!");
                return;
            }

            Corrida corridaSelecionada = emAndamento.get(escolha - 1);
            acompanharCorrida(corridaSelecionada, contexto, entrada);

        } catch (NumberFormatException e) {
            erro("Por favor, digite um número válido.");
        }
    }

    private void acompanharCorrida(Corrida corrida, ContextoAplicacao contexto, Scanner entrada) {
        ServicoLocalizacao servicoLoc = new ServicoLocalizacao(contexto.repositorioCorrida);
        
        System.out.println("\n🚗 ACOMPANHAMENTO DA CORRIDA " + corrida.getId().substring(0, 8));
        System.out.println("Pressione Enter para atualizar ou 's' para sair");
        
        try {
            while (true) {
                var locOpt = servicoLoc.obterLocalizacaoMotorista(corrida.getId());
                
                if (locOpt.isPresent()) {
                    var loc = locOpt.get();
                    
                    System.out.println("\n--- STATUS ATUAL ---");
                    System.out.printf("Motorista: %s%n", corrida.getMotoristaAlocado());
                    System.out.printf("Distância até você: %.1f km%n", loc.getDistanciaPassageiroKm());
                    System.out.printf("Tempo estimado: %d minutos%n", loc.getTempoEstimadoMinutos());
                    System.out.printf("Última atualização: %s%n", loc.getTimestamp().toLocalTime());
                    
                    // Simular progresso
                    servicoLoc.simularMovimentoMotorista(corrida.getId());
                } else {
                    System.out.println("Aguardando motorista...");
                }
                
                System.out.print("\nAtualizar? (Enter para atualizar, 's' para sair): ");
                String comando = entrada.nextLine().trim();
                if (comando.equalsIgnoreCase("s")) break;
                
                // Pequena pausa para simular tempo real
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ok("Acompanhamento encerrado.");
    }
}