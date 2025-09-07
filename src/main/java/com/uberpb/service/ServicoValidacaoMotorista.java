package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Motorista;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioUsuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServicoValidacaoMotorista {

    private RepositorioUsuario repositorioUsuario;

    public ServicoValidacaoMotorista(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Realiza a validação dos documentos e do veículo de um motorista, definindo suas categorias de atuação.
     * @param motorista O objeto Motorista a ser validado.
     * @param veiculo Os dados do veículo.
     * @param possuiCnhValida true se o motorista possuir CNH válida.
     * @param possuiCrlvValido true se o veículo possuir CRLV válido.
     * @return O motorista atualizado com as categorias de veículo e status de ativação.
     * @throws IllegalArgumentException Se as informações básicas de CNH/CRLV não forem válidas.
     */
    public Motorista validarVeiculoEDocumentos(Motorista motorista, Veiculo veiculo, boolean possuiCnhValida, boolean possuiCrlvValido) {
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista não pode ser nulo.");
        }
        if (!possuiCnhValida || !possuiCrlvValido) {
            motorista.setCnhValida(false);
            motorista.setCrlvValido(false);
            motorista.setContaAtiva(false);
            repositorioUsuario.atualizar(motorista);
            throw new IllegalArgumentException("Motorista e/ou veículo não possuem documentação válida. Conta não ativada.");
        }

        motorista.setCnhValida(possuiCnhValida);
        motorista.setCrlvValido(possuiCrlvValido);
        motorista.setVeiculo(veiculo);

        List<CategoriaVeiculo> categoriasQualificadas = new ArrayList<>();

        // Lógica para determinar as categorias com base nos dados do veículo (RF06)
        // UberX: Requisito básico, sempre incluído se o veículo for válido.
        categoriasQualificadas.add(CategoriaVeiculo.UBERX);

        // Uber Comfort: Carros mais novos e espaçosos
        if (veiculo.getAno() >= 2018 && veiculo.getCapacidadePassageiros() >= 4) {
            categoriasQualificadas.add(CategoriaVeiculo.COMFORT);
        }

        // Uber Black: Veículos premium e motoristas de alta avaliação
        // Para simplificar, consideramos "premium" por ano e alguns modelos de exemplo.
        // Motoristas de alta avaliação seria um RF17 futuro, aqui simulamos pela qualificação do veículo.
        List<String> modelosPremium = Arrays.asList("Mercedes-Benz", "Audi", "BMW", "Volvo", "Lexus");
        if (veiculo.getAno() >= 2020 && veiculo.getCapacidadePassageiros() >= 4 &&
            (veiculo.getCor().equalsIgnoreCase("preto") || veiculo.getCor().equalsIgnoreCase("cinza")) &&
            modelosPremium.stream().anyMatch(model -> veiculo.getModelo().toLowerCase().contains(model.toLowerCase()))) {
            categoriasQualificadas.add(CategoriaVeiculo.BLACK);
        }

        // Uber Bag: Veículos com porta-malas maior
        if (veiculo.getTamanhoPortaMalas().equalsIgnoreCase("G")) {
            categoriasQualificadas.add(CategoriaVeiculo.BAG);
        }

        // Uber XL: Capacidade para mais passageiros
        if (veiculo.getCapacidadePassageiros() >= 6) {
            categoriasQualificadas.add(CategoriaVeiculo.XL);
        }

        veiculo.setCategoriasDisponiveis(categoriasQualificadas);

        // Se o motorista tem CNH/CRLV válidos e o veículo se qualifica para pelo menos UberX, a conta é ativada.
        if (possuiCnhValida && possuiCrlvValido && !categoriasQualificadas.isEmpty()) {
            motorista.setContaAtiva(true);
            System.out.println("Motorista " + motorista.getEmail() + " ativado com sucesso para as categorias: " + categoriasQualificadas);
        } else {
            motorista.setContaAtiva(false);
            System.out.println("Motorista " + motorista.getEmail() + " não pôde ser ativado. Verifique os dados do veículo e documentos.");
        }

        repositorioUsuario.atualizar(motorista);
        return motorista;
    }
}