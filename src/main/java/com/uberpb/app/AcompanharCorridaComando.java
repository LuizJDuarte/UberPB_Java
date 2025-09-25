package com.uberpb.app; 

import com.uberpb.model.*;
import com.uberpb.service.ServicoLocalizacao;
import com.uberpb.service.ServicoOtimizacaoRota;
import com.uberpb.service.EstimativaChegada;
import com.uberpb.service.RotaOtimizada;

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
        ServicoLocalizacao servicoLoc = contexto.servicoLocalizacao;
        ServicoOtimizacaoRota servicoOtimizacao = contexto.servicoOtimizacaoRota;
        
        System.out.println("\n🚗 ACOMPANHAMENTO DA CORRIDA " + corrida.getId().substring(0, 8));
        System.out.println("Pressione Enter para atualizar ou 's' para sair");
        
        try {
            while (true) {
                // Usar nova estimativa de chegada
                EstimativaChegada estimativa = servicoLoc.calcularEstimativaChegada(corrida.getId());
                
                System.out.println("\n--- STATUS ATUAL ---");
                System.out.printf("Motorista: %s%n", corrida.getMotoristaAlocado());
                System.out.printf("Distância até você: %.1f km%n", estimativa.getDistanciaKm());
                System.out.printf("Tempo estimado: %d minutos%n", estimativa.getTempoEstimadoMinutos());
                System.out.printf("Precisão: %s%n", estimativa.getPrecisao());
                
                // Mostrar informações de otimização se disponível
                if (corrida.getOrigem() != null && corrida.getDestino() != null) {
                    RotaOtimizada rota = servicoOtimizacao.calcularRotaOtimizada(
                        corrida.getOrigem(), corrida.getDestino());
                    System.out.printf("Rota otimizada: %.1f km (Economia: %.0f%%)%n", 
                        rota.getDistanciaKm(), rota.getEconomiaTempoPercentual());
                }
                
                System.out.print("\nAtualizar? (Enter para atualizar, 's' para sair): ");
                String comando = entrada.nextLine().trim();
                if (comando.equalsIgnoreCase("s")) break;
                
                Thread.sleep(3000); // Aumentar para 3 segundos para melhor experiência
                
                // Simular movimento do motorista
                servicoLoc.simularMovimentoMotorista(corrida.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ok("Acompanhamento encerrado.");
    }
}