package com.uberpb.app;

import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.RotaOtimizada;
import com.uberpb.service.ServicoOtimizacaoRota;

import java.util.Scanner;

public class OtimizarRotaComando implements Comando {

    @Override
    public String nome() {
        return "Otimizar Rota";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("=== OTIMIZAÇÃO DE ROTA ===");
        
        System.out.print("Latitude de origem: ");
        double latOrigem = Double.parseDouble(entrada.nextLine());
        System.out.print("Longitude de origem: ");
        double lonOrigem = Double.parseDouble(entrada.nextLine());
        
        System.out.print("Latitude de destino: ");
        double latDestino = Double.parseDouble(entrada.nextLine());
        System.out.print("Longitude de destino: ");
        double lonDestino = Double.parseDouble(entrada.nextLine());
        
        var origem = new com.uberpb.model.Localizacao(latOrigem, lonOrigem);
        var destino = new com.uberpb.model.Localizacao(latDestino, lonDestino);
        
        ServicoOtimizacaoRota servico = contexto.servicoOtimizacaoRota;
        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);
        
        System.out.println("\n--- ROTA OTIMIZADA ---");
        System.out.printf("Distância: %.1f km%n", rota.getDistanciaKm());
        System.out.printf("Tempo estimado: %.0f minutos%n", rota.getTempoEstimadoMinutos());
        System.out.printf("Economia de tempo: %.1f%%%n", rota.getEconomiaTempoPercentual());
        System.out.printf("Pontos da rota: %d%n", rota.getPontosRota().size());
    }
}