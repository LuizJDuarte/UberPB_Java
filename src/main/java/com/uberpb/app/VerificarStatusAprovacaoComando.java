package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;

import java.util.Scanner;

public class VerificarStatusAprovacaoComando implements Comando {

    @Override
    public String nome() {
        return "Verificar Status de Aprovação";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        // Visível se for um motorista, JÁ TIVER VEÍCULO, mas ainda não estiver totalmente aprovado.
        if (usuario instanceof Motorista motorista) {
            if (motorista.getVeiculo() == null) {
                // Se não tem veículo, o comando a ser visto é o de completar cadastro.
                return false;
            }
            boolean aprovado = motorista.isCnhValida() &&
                               motorista.isCrlvValido() &&
                               motorista.isContaAtiva();
            return !aprovado;
        }
        return false;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        Motorista motorista = (Motorista) contexto.getSessao().getUsuarioAtual();
        if (motorista == null) {
            ConsoleUI.erro("Nenhum motorista logado.");
            return;
        }

        ConsoleUI.cabecalho("Status da Sua Conta", null);
        System.out.println("Olá, " + motorista.getEmail() + "!");
        System.out.println("Este é o status atual do seu cadastro:");
        System.out.println();
        System.out.printf("  - Cadastro de Veículo: %s%n", formatarStatus(motorista.getVeiculo() != null));
        System.out.printf("  - Validação da CNH:    %s%n", formatarStatus(motorista.isCnhValida()));
        System.out.printf("  - Validação do CRLV:   %s%n", formatarStatus(motorista.isCrlvValido()));
        System.out.printf("  - Ativação da Conta:   %s%n", formatarStatus(motorista.isContaAtiva()));
        System.out.println();
        System.out.println("Sua conta precisa ser totalmente aprovada por um administrador para que você possa começar a dirigir.");
        System.out.println("Por favor, aguarde a análise dos seus documentos.");
    }

    private String formatarStatus(boolean status) {
        return status ? "✅ Aprovado" : "⏳ Pendente";
    }
}
