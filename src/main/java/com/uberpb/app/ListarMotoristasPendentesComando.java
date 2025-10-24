package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ListarMotoristasPendentesComando implements Comando {

    @Override
    public String nome() {
        return "Listar Motoristas Pendentes";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario instanceof Administrador;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        List<Motorista> pendentes = contexto.repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Motorista)
                .map(u -> (Motorista) u)
                .filter(m -> !m.isCnhValida() || !m.isCrlvValido() || !m.isContaAtiva())
                .collect(Collectors.toList());

        ConsoleUI.cabecalho("Motoristas com Cadastro Pendente", null);
        if (pendentes.isEmpty()) {
            System.out.println("Nenhum motorista com pendências encontrado.");
            return;
        }

        for (Motorista m : pendentes) {
            System.out.println("----------------------------------------");
            System.out.println("Email: " + m.getEmail());
            System.out.printf("  - CNH Válida: %s%n", formatarStatus(m.isCnhValida()));
            System.out.printf("  - CRLV Válido: %s%n", formatarStatus(m.isCrlvValido()));
            System.out.printf("  - Conta Ativa: %s%n", formatarStatus(m.isContaAtiva()));
            if (m.getVeiculo() != null) {
                System.out.println("  - Veículo: " + m.getVeiculo().getModelo() + " (" + m.getVeiculo().getPlaca() + ")");
            } else {
                System.out.println("  - Veículo: Não cadastrado");
            }
        }
        System.out.println("----------------------------------------");
    }

    private String formatarStatus(boolean status) {
        return status ? "Sim" : "Não";
    }
}
