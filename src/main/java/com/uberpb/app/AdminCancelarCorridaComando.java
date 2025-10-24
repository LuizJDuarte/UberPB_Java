package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import java.util.List;
import java.util.Scanner;

public class AdminCancelarCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Cancelar Corrida (Admin)";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull != null && usuarioAtualOuNull.getTipo() == com.uberpb.model.TipoUsuario.ADMIN;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        RepositorioCorrida repositorioCorrida = contexto.repositorioCorrida;
        RepositorioUsuario repositorioUsuario = contexto.repositorioUsuario;

        System.out.println("Lista de todas as corridas:");
        List<Corrida> corridas = repositorioCorrida.buscarTodas();
        if (corridas.isEmpty()) {
            System.out.println("Nenhuma corrida encontrada.");
            return;
        }

        for (int i = 0; i < corridas.size(); i++) {
            Corrida corrida = corridas.get(i);
            Usuario passageiro = repositorioUsuario.buscarPorEmail(corrida.getEmailPassageiro());
            Usuario motorista = null;
            if (corrida.getMotoristaAlocado() != null) {
                motorista = repositorioUsuario.buscarPorEmail(corrida.getMotoristaAlocado());
            }

            String nomePassageiro = (passageiro != null) ? passageiro.getEmail() : "N/A";
            String nomeMotorista = (motorista != null) ? motorista.getEmail() : "N/A";

            System.out.println((i + 1) + ". " +
                    "ID: " + corrida.getId() + ", " +
                    "Passageiro: " + nomePassageiro + ", " +
                    "Motorista: " + nomeMotorista + ", " +
                    "Origem: " + corrida.getOrigemEndereco() + ", " +
                    "Destino: " + corrida.getDestinoEndereco() + ", " +
                    "Status: " + corrida.getStatus());
        }

        System.out.print("Digite o ID da corrida que deseja cancelar: ");
        String corridaId = entrada.nextLine();

        Corrida corridaParaCancelar = repositorioCorrida.buscarPorId(corridaId);

        if (corridaParaCancelar != null) {
            corridaParaCancelar.setStatus(CorridaStatus.CANCELADA);
            repositorioCorrida.atualizar(corridaParaCancelar);
            System.out.println("Corrida " + corridaId + " cancelada com sucesso.");
        } else {
            System.out.println("Corrida com ID " + corridaId + " não encontrada.");
        }
    }
}
