package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import java.util.Scanner;

public class CompletarCadastroMotoristaComando implements Comando {

    @Override
    public String nome() {
        return "Completar Cadastro (Veículo)";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        if (usuario instanceof Motorista) {
            Motorista motorista = (Motorista) usuario;
            return motorista.getVeiculo() == null;
        }
        return false;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        ConsoleUI.cabecalho("Cadastro de Veículo", contexto.getSessao());
        System.out.print("Modelo do Veículo: ");
        String modelo = scanner.nextLine();
        System.out.print("Placa do Veículo: ");
        String placa = scanner.nextLine();
        System.out.print("Cor do Veículo: ");
        String cor = scanner.nextLine();
        System.out.print("Ano do Veículo: ");
        int ano = Integer.parseInt(scanner.nextLine());
        System.out.print("Capacidade de Passageiros: ");
        int capacidade = Integer.parseInt(scanner.nextLine());
        System.out.print("Tamanho do Porta-Malas (P/M/G): ");
        String portaMalas = scanner.nextLine();


        Veiculo veiculo = new Veiculo(modelo, ano, placa, cor, capacidade, portaMalas);
        Motorista motorista = (Motorista) contexto.getSessao().getUsuarioAtual();
        motorista.setVeiculo(veiculo);
        contexto.getRepositorioUsuario().salvar(motorista);

        ConsoleUI.ok("Veículo cadastrado com sucesso!");
    }
}
