package com.uberpb.service;

import com.uberpb.exceptions.CorridaAindaEmAndamentoException;
import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;
    private EstimativaCorrida ultimaEstimativa;

    public ServicoCorrida(RepositorioCorrida rc, RepositorioUsuario ru) {
        this.repositorioCorrida = rc;
        this.repositorioUsuario = ru;
    }

    public Corrida solicitarCorrida(String emailPassageiro,
                                    String origemEndereco,
                                    String destinoEndereco,
                                    CategoriaVeiculo categoria,
                                    MetodoPagamento metodoPagamento) {
        if (!(repositorioUsuario.buscarPorEmail(emailPassageiro) instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");
        if (origemEndereco == null || destinoEndereco == null || origemEndereco.isBlank() || destinoEndereco.isBlank())
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        if (origemEndereco.trim().equalsIgnoreCase(destinoEndereco.trim()))
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");

        Corrida c = Corrida.novaComEnderecos(emailPassageiro,
                origemEndereco.trim(), destinoEndereco.trim(), categoria, metodoPagamento);
        c.setStatus(CorridaStatus.SOLICITADA);
        repositorioCorrida.salvar(c);
        return c;
    }

    public EstimativaCorrida estimarPorEnderecos(String origemEndereco, String destinoEndereco, CategoriaVeiculo categoria) {
        double dist = estimarDistanciaHeuristica(origemEndereco, destinoEndereco);
        int minutos = (int)Math.round(dist * 3.0);
        double preco = precoBase(categoria) + dist * precoKm(categoria) + minutos * precoMin(categoria);
        preco = Math.round(preco * 100.0) / 100.0;
        ultimaEstimativa = new EstimativaCorrida(dist, minutos, preco);
        return ultimaEstimativa;
    }

    public void iniciarSeAceita(Corrida corrida, GerenciadorCorridasAtivas ger) {
        if (ultimaEstimativa == null) return;
        ger.iniciar(corrida.getId(), ultimaEstimativa.getDistanciaKm(), ultimaEstimativa.getMinutos());
        corrida.setStatus(CorridaStatus.ACEITA);
        repositorioCorrida.atualizar(corrida);
    }

    public void concluirCorrida(String corridaId, String emailSolicitante, GerenciadorCorridasAtivas ger) {
        Corrida c = repositorioCorrida.buscarPorId(corridaId);
        if (c == null) throw new IllegalArgumentException("Corrida não encontrada.");
        if (!emailSolicitante.equalsIgnoreCase(c.getEmailPassageiro()))
            throw new IllegalArgumentException("Apenas o passageiro pode concluir a corrida.");
        var p = ger.progresso(c.getId());
        if (p != null && !p.isConcluida())
            throw new CorridaAindaEmAndamentoException("Ainda não atingiu o tempo/distância estimados.");
        repositorioCorrida.atualizar(c);
        ger.encerrar(c.getId());
    }

    private double precoBase(CategoriaVeiculo cat) {
        if (cat == null) return 3.5;
        return switch (cat) {
            case COMFORT -> 4.5;
            case BLACK -> 7.0;
            case BAG -> 3.85;
            case XL -> 4.55;
            default -> 3.5;
        };
    }
    private double precoKm(CategoriaVeiculo cat) {
        if (cat == null) return 1.2;
        return switch (cat) {
            case COMFORT -> 1.6;
            case BLACK -> 2.4;
            case BAG -> 1.32;
            case XL -> 1.56;
            default -> 1.2;
        };
    }
    private double precoMin(CategoriaVeiculo cat) {
        if (cat == null) return 0.5;
        return switch (cat) {
            case COMFORT -> 0.6;
            case BLACK -> 0.8;
            case BAG -> 0.55;
            case XL -> 0.65;
            default -> 0.5;
        };
    }
    private double estimarDistanciaHeuristica(String o, String d) {
        int h = Math.abs((o + "|" + d).hashCode());
        return 4.0 + (h % 10);
    }
}
