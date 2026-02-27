package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.*;
import com.uberpb.model.Corrida;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.List;
import java.util.Scanner;

public class AvaliarCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Avaliar Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro || usuarioAtualOuNull instanceof Motorista;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String usuarioEmail = contexto.sessao.getUsuarioAtual().getEmail();
        
        System.out.println("=== AVALIAR CORRIDA ===");
        
        // Buscar corridas disponíveis para avaliação
        List<Corrida> corridasParaAvaliar = contexto.servicoAvaliacao.getCorridasParaAvaliar(usuarioEmail);
        
        if (corridasParaAvaliar.isEmpty()) {
            System.out.println("📝 Nenhuma corrida disponível para avaliação no momento.");
            System.out.println("   - As corridas precisam estar CONCLUÍDAS");
            System.out.println("   - E ainda não terem sido avaliadas");
            return;
        }
        
        // Listar corridas
        System.out.println("Selecione a corrida para avaliar:");
        for (int i = 0; i < corridasParaAvaliar.size(); i++) {
            Corrida c = corridasParaAvaliar.get(i);
            String tipoUsuario = c.getEmailPassageiro().equals(usuarioEmail) ? "👤 Passageiro" : "🚗 Motorista";
            System.out.printf("%d) %s - %s → %s (%s)%n", 
                i + 1, c.getId().substring(0, 8), 
                c.getOrigemEndereco() != null ? c.getOrigemEndereco() : "Origem",
                c.getDestinoEndereco() != null ? c.getDestinoEndereco() : "Destino",
                tipoUsuario);
        }
        
        System.out.print("Escolha a corrida (número): ");
        try {
            int escolha = Integer.parseInt(entrada.nextLine().trim());
            if (escolha < 1 || escolha > corridasParaAvaliar.size()) {
                erro("Opção inválida!");
                return;
            }
            
            Corrida corridaSelecionada = corridasParaAvaliar.get(escolha - 1);
            processarAvaliacao(corridaSelecionada, usuarioEmail, contexto, entrada);
            
        } catch (NumberFormatException e) {
            erro("Por favor, digite um número válido.");
        }
    }
    
    private void processarAvaliacao(Corrida corrida, String usuarioEmail, ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("\n--- AVALIAÇÃO DA CORRIDA " + corrida.getId().substring(0, 8) + " ---");
        
        // Determinar quem está avaliando quem
        boolean isPassageiroAvaliando = corrida.getEmailPassageiro().equals(usuarioEmail);
        String avaliador = isPassageiroAvaliando ? "Passageiro" : "Motorista";
        String avaliado = isPassageiroAvaliando ? "Motorista" : "Passageiro";
        String emailAvaliado = isPassageiroAvaliando ? corrida.getMotoristaAlocado() : corrida.getEmailPassageiro();
        
        System.out.printf("Você (%s) está avaliando o %s: %s%n", avaliador, avaliado.toLowerCase(), emailAvaliado);
        
        // Solicitar rating
        System.out.println("\n⭐ Como você avalia esta corrida?");
        System.out.println("1) ⭐☆☆☆☆ - Ruim");
        System.out.println("2) ⭐⭐☆☆☆ - Regular");
        System.out.println("3) ⭐⭐⭐☆☆ - Bom");
        System.out.println("4) ⭐⭐⭐⭐☆ - Muito Bom");
        System.out.println("5) ⭐⭐⭐⭐⭐ - Excelente");
        System.out.print("Digite o número de estrelas (1-5): ");
        
        int rating;
        try {
            rating = Integer.parseInt(entrada.nextLine().trim());
            if (rating < 1 || rating > 5) {
                erro("Rating deve ser entre 1 e 5!");
                return;
            }
        } catch (NumberFormatException e) {
            erro("Por favor, digite um número válido.");
            return;
        }
        
        // Solicitar comentário (opcional)
        System.out.print("💬 Comentário (opcional - Enter para pular): ");
        String comentario = entrada.nextLine().trim();
        if (comentario.isEmpty()) {
            comentario = "Sem comentário";
        }
        
        // Confirmar avaliação
        System.out.printf("%n📋 Resumo da avaliação:%n");
        System.out.printf("   Rating: %d estrelas%n", rating);
        System.out.printf("   Comentário: %s%n", comentario);
        System.out.print("Confirmar avaliação? (s/N): ");
        
        String confirmacao = entrada.nextLine().trim();
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Avaliação cancelada.");
            return;
        }
        
        // Processar avaliação
        try {
            if (isPassageiroAvaliando) {
                contexto.servicoAvaliacao.avaliarMotorista(corrida.getId(), usuarioEmail, rating, comentario);
            } else {
                contexto.servicoAvaliacao.avaliarPassageiro(corrida.getId(), usuarioEmail, rating, comentario);
            }
            
            ok("✅ Avaliação registrada com sucesso!");
            System.out.printf("   %s agora tem uma nova avaliação de %d estrelas!%n", avaliado, rating);
            
        } catch (Exception e) {
            erro("Erro ao registrar avaliação: " + e.getMessage());
        }
    }
}