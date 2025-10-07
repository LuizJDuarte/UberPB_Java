package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class ConcluirCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Concluir Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro || usuarioAtualOuNull instanceof Motorista;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String usuarioEmail = contexto.sessao.getUsuarioAtual().getEmail();
        
        System.out.println("=== CONCLUIR CORRIDA ===");
        
        // Buscar corridas em andamento do usuário
        List<Corrida> corridasEmAndamento = getCorridasEmAndamento(contexto, usuarioEmail);
        
        if (corridasEmAndamento.isEmpty()) {
            System.out.println("📭 Nenhuma corrida em andamento no momento.");
            return;
        }
        
        // Listar corridas
        System.out.println("Selecione a corrida para concluir:");
        for (int i = 0; i < corridasEmAndamento.size(); i++) {
            Corrida c = corridasEmAndamento.get(i);
            String tipoUsuario = c.getEmailPassageiro().equals(usuarioEmail) ? "👤 Passageiro" : "🚗 Motorista";
            String outraParte = c.getEmailPassageiro().equals(usuarioEmail) ? 
                c.getMotoristaAlocado() : c.getEmailPassageiro();
                
            System.out.printf("%d) %s - %s → %s (%s)%n", 
                i + 1, c.getId().substring(0, 8), 
                c.getOrigemEndereco() != null ? c.getOrigemEndereco() : "Origem",
                c.getDestinoEndereco() != null ? c.getDestinoEndereco() : "Destino",
                tipoUsuario);
            System.out.printf("   👥 Com: %s%n", outraParte);
        }
        
        System.out.print("Escolha a corrida (número): ");
        try {
            int escolha = Integer.parseInt(entrada.nextLine().trim());
            if (escolha < 1 || escolha > corridasEmAndamento.size()) {
                erro("Opção inválida!");
                return;
            }
            
            Corrida corridaSelecionada = corridasEmAndamento.get(escolha - 1);
            
            // Confirmar conclusão
            System.out.printf("%n📋 Confirmar conclusão da corrida %s?%n", corridaSelecionada.getId().substring(0, 8));
            System.out.printf("   De: %s%n", corridaSelecionada.getOrigemEndereco());
            System.out.printf("   Para: %s%n", corridaSelecionada.getDestinoEndereco());
            System.out.print("Digite 'CONCLUIR' para confirmar: ");
            
            String confirmacao = entrada.nextLine().trim();
            if (!confirmacao.equalsIgnoreCase("CONCLUIR")) {
                System.out.println("Conclusão cancelada.");
                return;
            }
            
            // Concluir corrida
            contexto.servicoCorrida.concluirCorrida(corridaSelecionada.getId(), usuarioEmail);
            
            ok("✅ Corrida concluída com sucesso!");
            System.out.println("📝 Agora você pode avaliar a corrida no menu 'Avaliar Corrida'.");
            
        } catch (NumberFormatException e) {
            erro("Por favor, digite um número válido.");
        } catch (Exception e) {
            erro("Erro ao concluir corrida: " + e.getMessage());
        }
    }
    
    private List<Corrida> getCorridasEmAndamento(ContextoAplicacao contexto, String usuarioEmail) {
        List<Corrida> todasCorridas = contexto.repositorioCorrida.buscarTodas();
        List<Corrida> emAndamento = new ArrayList<>();
        
        for (Corrida corrida : todasCorridas) {
            boolean isUsuarioDaCorrida = corrida.getEmailPassageiro().equals(usuarioEmail) ||
                                        (corrida.getMotoristaAlocado() != null && 
                                         corrida.getMotoristaAlocado().equals(usuarioEmail));
            
            if (isUsuarioDaCorrida && corrida.getStatus() == CorridaStatus.EM_ANDAMENTO) {
                emAndamento.add(corrida);
            }
        }
        
        return emAndamento;
    }
}