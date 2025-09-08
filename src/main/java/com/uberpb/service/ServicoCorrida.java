package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoCorrida(RepositorioCorrida repositorioCorrida, RepositorioUsuario repositorioUsuario) {
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }

    /** RF04 por endereço (texto) - método original para compatibilidade. */
    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco) {
        validarSolicitacaoBasica(emailPassageiro, origemEndereco, destinoEndereco);
        
        Corrida corrida = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim());
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** RF05 - Novo método com categoria e preço estimado. */
    public Corrida solicitarCorridaComCategoria(String emailPassageiro, String origemEndereco, 
                                              String destinoEndereco, CategoriaVeiculo categoria, 
                                              Double precoEstimado) {
        validarSolicitacaoBasica(emailPassageiro, origemEndereco, destinoEndereco);
        
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }
        
        if (precoEstimado == null || precoEstimado <= 0) {
            throw new IllegalArgumentException("Preço estimado deve ser maior que zero.");
        }

        Corrida corrida = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), 
                                                 destinoEndereco.trim(), categoria, precoEstimado);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** RF05 - Método para calcular estimativa sem criar corrida. */
    public EstimativaCorrida calcularEstimativa(String origemEndereco, String destinoEndereco) {
        if (origemEndereco == null || origemEndereco.isBlank() || 
            destinoEndereco == null || destinoEndereco.isBlank()) {
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        }

        if (origemEndereco.trim().equalsIgnoreCase(destinoEndereco.trim())) {
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");
        }

        double distanciaEstimada = CalculadoraPrecoCorrida.estimarDistanciaKm(origemEndereco, destinoEndereco);
        double tempoEstimado = CalculadoraPrecoCorrida.estimarTempoMinutos(distanciaEstimada);

        EstimativaCorrida estimativa = new EstimativaCorrida(
            origemEndereco.trim(), 
            destinoEndereco.trim(),
            distanciaEstimada,
            tempoEstimado
        );

        // Calcular preços para todas as categorias
        for (CategoriaVeiculo categoria : CategoriaVeiculo.values()) {
            double preco = CalculadoraPrecoCorrida.calcularPreco(distanciaEstimada, tempoEstimado, categoria);
            estimativa.adicionarPrecoCategoria(categoria, preco);
        }

        return estimativa;
    }

    /** Mantido para futuros usos: por coordenadas. */
    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino) {
        if (emailPassageiro == null || emailPassageiro.isBlank()) {
            throw new IllegalArgumentException("Passageiro inválido.");
        }
        
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        }

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro)) {
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");
        }

        if (Double.compare(origem.latitude(), destino.latitude()) == 0 &&
            Double.compare(origem.longitude(), destino.longitude()) == 0) {
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");
        }

        Corrida corrida = Corrida.novaComCoordenadas(emailPassageiro, origem, destino);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** RF05 - Método auxiliar para validações comuns. */
    private void validarSolicitacaoBasica(String emailPassageiro, String origemEndereco, String destinoEndereco) {
        if (emailPassageiro == null || emailPassageiro.isBlank()) {
            throw new IllegalArgumentException("Passageiro inválido.");
        }
        
        if (origemEndereco == null || origemEndereco.isBlank() || 
            destinoEndereco == null || destinoEndereco.isBlank()) {
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        }

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro)) {
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");
        }

        if (origemEndereco.trim().equalsIgnoreCase(destinoEndereco.trim())) {
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");
        }
    }

    /** RF05 - Classe interna para representar estimativas de corrida. */
    public static class EstimativaCorrida {
        private final String origem;
        private final String destino;
        private final double distanciaKm;
        private final double tempoMinutos;
        private final java.util.Map<CategoriaVeiculo, Double> precosPorCategoria;

        public EstimativaCorrida(String origem, String destino, double distanciaKm, double tempoMinutos) {
            this.origem = origem;
            this.destino = destino;
            this.distanciaKm = distanciaKm;
            this.tempoMinutos = tempoMinutos;
            this.precosPorCategoria = new java.util.HashMap<>();
        }

        public void adicionarPrecoCategoria(CategoriaVeiculo categoria, double preco) {
            precosPorCategoria.put(categoria, preco);
        }

        public double getPrecoCategoria(CategoriaVeiculo categoria) {
            return precosPorCategoria.getOrDefault(categoria, 0.0);
        }

        public java.util.Map<CategoriaVeiculo, Double> getPrecosPorCategoria() {
            return new java.util.HashMap<>(precosPorCategoria);
        }

        public String getOrigem() { return origem; }
        public String getDestino() { return destino; }
        public double getDistanciaKm() { return distanciaKm; }
        public double getTempoMinutos() { return tempoMinutos; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Estimativa de Corrida:\n")
              .append("? Origem: ").append(origem).append("\n")
              .append("? Destino: ").append(destino).append("\n")
              .append("? Distância: ").append(String.format("%.1f", distanciaKm)).append(" km\n")
              .append("? Tempo estimado: ").append(String.format("%.0f", tempoMinutos)).append(" minutos\n\n")
              .append("Preços por categoria:\n");
            
            for (java.util.Map.Entry<CategoriaVeiculo, Double> entry : precosPorCategoria.entrySet()) {
                sb.append("? ").append(entry.getKey().getNome())
                  .append(": R$ ").append(String.format("%.2f", entry.getValue()))
                  .append(" - ").append(entry.getKey().getDescricao()).append("\n");
            }
            
            return sb.toString();
        }
    }

    /** RF05 - Método para buscar corridas por passageiro com informações completas. */
    public java.util.List<Corrida> buscarCorridasCompletasPorPassageiro(String emailPassageiro) {
        if (emailPassageiro == null || emailPassageiro.isBlank()) {
            throw new IllegalArgumentException("Email do passageiro é obrigatório.");
        }

        return repositorioCorrida.buscarPorPassageiro(emailPassageiro);
    }

    /** RF05 - Método para atualizar preço de uma corrida existente. */
    public void atualizarPrecoCorrida(String idCorrida, Double novoPreco) {
        if (idCorrida == null || idCorrida.isBlank()) {
            throw new IllegalArgumentException("ID da corrida é obrigatório.");
        }
        
        if (novoPreco == null || novoPreco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }

        Corrida corrida = repositorioCorrida.buscarPorId(idCorrida);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + idCorrida);
        }

        corrida.setPrecoEstimado(novoPreco);
        repositorioCorrida.atualizar(corrida);
    }

    /** RF05 - Método para atualizar categoria de uma corrida existente. */
    public void atualizarCategoriaCorrida(String idCorrida, CategoriaVeiculo novaCategoria) {
        if (idCorrida == null || idCorrida.isBlank()) {
            throw new IllegalArgumentException("ID da corrida é obrigatório.");
        }
        
        if (novaCategoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }

        Corrida corrida = repositorioCorrida.buscarPorId(idCorrida);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + idCorrida);
        }

        corrida.setCategoria(novaCategoria);
        repositorioCorrida.atualizar(corrida);
    }
}