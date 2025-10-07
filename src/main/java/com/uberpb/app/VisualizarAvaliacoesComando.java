package com.uberpb.app;

import com.uberpb.model.Avaliacao;
import com.uberpb.model.AvaliacaoMotorista;
import com.uberpb.model.AvaliacaoPassageiro;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class VisualizarAvaliacoesComando implements Comando {

    @Override
    public String nome() {
        return "Visualizar Minhas Avaliações";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro || usuarioAtualOuNull instanceof Motorista;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String usuarioEmail = contexto.sessao.getUsuarioAtual().getEmail();
        Usuario usuario = contexto.sessao.getUsuarioAtual();
        
        System.out.println("=== MINHAS AVALIAÇÕES ===");
        
        // Exibir rating atual
        if (usuario instanceof Passageiro passageiro) {
            System.out.printf("⭐ Seu Rating: %.1f (%d avaliações)%n%n", 
                passageiro.getRatingMedio(), passageiro.getTotalAvaliacoes());
        } else if (usuario instanceof Motorista motorista) {
            System.out.printf("⭐ Seu Rating: %.1f (%d avaliações)%n%n", 
                motorista.getRatingMedio(), motorista.getTotalAvaliacoes());
        }
        
        // Buscar avaliações recebidas
        List<Avaliacao> avaliacoesRecebidas;
        if (usuario instanceof Passageiro) {
            avaliacoesRecebidas = contexto.servicoAvaliacao.getAvaliacoesPassageiro(usuarioEmail);
        } else {
            avaliacoesRecebidas = contexto.servicoAvaliacao.getAvaliacoesMotorista(usuarioEmail);
        }
        
        if (avaliacoesRecebidas.isEmpty()) {
            System.out.println("📝 Você ainda não recebeu nenhuma avaliação.");
        } else {
            System.out.println("📋 AVALIAÇÕES RECEBIDAS:");
            System.out.println("──────────────────────────────────────────────");
            
            for (int i = 0; i < avaliacoesRecebidas.size(); i++) {
                Avaliacao av = avaliacoesRecebidas.get(i);
                exibirAvaliacao(av, i + 1, contexto);
            }
            System.out.println("──────────────────────────────────────────────");
        }
        
        // Buscar avaliações feitas (apenas para motoristas)
        if (usuario instanceof Motorista) {
            List<Avaliacao> avaliacoesFeitas = contexto.servicoAvaliacao.getAvaliacoesPassageiro(usuarioEmail);
            if (!avaliacoesFeitas.isEmpty()) {
                System.out.println("\n📝 AVALIAÇÕES FEITAS A PASSAGEIROS:");
                System.out.println("──────────────────────────────────────────────");
                
                for (int i = 0; i < avaliacoesFeitas.size(); i++) {
                    Avaliacao av = avaliacoesFeitas.get(i);
                    if (av instanceof AvaliacaoMotorista am) {
                        System.out.printf("%d) ⭐ %d/5 - Para: %s%n", 
                            i + 1, am.getRating(), am.getPassageiroEmail());
                        if (!am.getComentario().equals("Sem comentário")) {
                            System.out.printf("   💬 \"%s\"%n", am.getComentario());
                        }
                        System.out.printf("   🆔 Corrida: %s%n", am.getCorridaId().substring(0, 8));
                        System.out.println();
                    }
                }
                System.out.println("──────────────────────────────────────────────");
            }
        }
        
        // Opção para ver estatísticas detalhadas
        if (!avaliacoesRecebidas.isEmpty()) {
            System.out.print("\nVer estatísticas detalhadas? (s/N): ");
            String opcao = entrada.nextLine().trim();
            if (opcao.equalsIgnoreCase("s")) {
                exibirEstatisticasDetalhadas(avaliacoesRecebidas);
            }
        }
    }
    
    private void exibirAvaliacao(Avaliacao avaliacao, int numero, ContextoAplicacao contexto) {
        String estrelas = "⭐".repeat(avaliacao.getRating()) + "☆".repeat(5 - avaliacao.getRating());
        
        if (avaliacao instanceof AvaliacaoPassageiro ap) {
            // Avaliação de passageiro para motorista
            System.out.printf("%d) %s %s%n", numero, estrelas, ap.getPassageiroEmail());
            if (!ap.getComentario().equals("Sem comentário")) {
                System.out.printf("   💬 \"%s\"%n", ap.getComentario());
            }
        } else if (avaliacao instanceof AvaliacaoMotorista am) {
            // Avaliação de motorista para passageiro
            System.out.printf("%d) %s %s%n", numero, estrelas, am.getMotoristaEmail());
            if (!am.getComentario().equals("Sem comentário")) {
                System.out.printf("   💬 \"%s\"%n", am.getComentario());
            }
        }
        
        System.out.printf("   🆔 Corrida: %s%n", avaliacao.getCorridaId().substring(0, 8));
        System.out.printf("   📅 Data: %s%n", 
            avaliacao.getDataAvaliacao().toLocalDate().toString());
        System.out.println();
    }
    
    private void exibirEstatisticasDetalhadas(List<Avaliacao> avaliacoes) {
        int[] contagemPorRating = new int[6]; // índices 1-5
        
        for (Avaliacao av : avaliacoes) {
            contagemPorRating[av.getRating()]++;
        }
        
        System.out.println("\n📊 ESTATÍSTICAS DETALHADAS:");
        System.out.println("──────────────────────────────────────────────");
        
        for (int i = 5; i >= 1; i--) {
            int quantidade = contagemPorRating[i];
            double percentual = (double) quantidade / avaliacoes.size() * 100;
            String barra = "█".repeat((int) (percentual / 5));
            
            System.out.printf("⭐ %d estrelas: %2d avaliações %s %.1f%%%n", 
                i, quantidade, barra, percentual);
        }
        
        System.out.println("──────────────────────────────────────────────");
        System.out.printf("📈 Total: %d avaliações%n", avaliacoes.size());
    }
}