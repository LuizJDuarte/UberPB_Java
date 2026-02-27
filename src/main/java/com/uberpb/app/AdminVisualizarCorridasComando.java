package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.Usuario;
import com.uberpb.model.TipoUsuario;
import java.util.List;
import java.util.Scanner;

public class AdminVisualizarCorridasComando implements Comando {

    @Override
    public String nome() {
        return "Visualizar Todas as Corridas";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null && usuario.getTipo() == TipoUsuario.ADMIN;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        ConsoleUI.cabecalho("Todas as Corridas", contexto.getSessao());
        List<Corrida> corridas = contexto.getRepositorioCorrida().buscarTodas();

        if (corridas.isEmpty()) {
            System.out.println("Nenhuma corrida encontrada.");
            return;
        }

        for (Corrida corrida : corridas) {
            System.out.println("ID: " + corrida.getId());
            System.out.println("Passageiro: " + corrida.getEmailPassageiro());
            System.out.println("Motorista: " + (corrida.getMotoristaAlocado() != null ? corrida.getMotoristaAlocado() : "N/A"));
            System.out.println("Origem: " + corrida.getOrigemEndereco());
            System.out.println("Destino: " + corrida.getDestinoEndereco());
            System.out.println("Status: " + corrida.getStatus());
            // System.out.println("Valor: " + corrida.getValor());
            System.out.println("--------------------");
        }
    }
}
