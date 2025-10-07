package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Localizacao;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Corrida — RF04..RF06.
 * Aceita categoria escolhida e calcula estimativa.
 * Mantém wrappers sem categoria para compatibilidade.
 */
public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoCorrida(RepositorioCorrida repositorioCorrida, RepositorioUsuario repositorioUsuario) {
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }

    // --------- NOVAS ASSINATURAS (com categoria) ---------

    /** RF04 + RF06: por endereços com categoria */
    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco, CategoriaVeiculo categoria, MetodoPagamento metodoPagamento) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origemEndereco == null || origemEndereco.isBlank()
                || destinoEndereco == null || destinoEndereco.isBlank())
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        if (origemEndereco.trim().equalsIgnoreCase(destinoEndereco.trim()))
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");

        Corrida corrida = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim(), categoria, metodoPagamento);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** RF04 + RF06: por coordenadas com categoria */
    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino, CategoriaVeiculo categoria, MetodoPagamento metodoPagamento) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origem == null || destino == null)
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        if (Double.compare(origem.latitude(), destino.latitude()) == 0 &&
            Double.compare(origem.longitude(), destino.longitude()) == 0)
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");

        Corrida corrida = Corrida.novaComCoordenadas(emailPassageiro, origem, destino, categoria, metodoPagamento);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    // --------- Wrappers de compatibilidade (sem categoria) ---------

    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco) {
        return solicitarCorrida(emailPassageiro, origemEndereco, destinoEndereco, null, MetodoPagamento.DINHEIRO);
    }

    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino) {
        return solicitarCorrida(emailPassageiro, origem, destino, null, MetodoPagamento.DINHEIRO);
    }

    // --------- RF05: Estimativa (tempo e preço) ---------

    public EstimativaCorrida estimarPorEnderecos(String origemEndereco, String destinoEndereco, CategoriaVeiculo categoria) {
        double distanciaKm = estimarDistanciaHeuristica(origemEndereco, destinoEndereco);
        return calcularEstimativa(distanciaKm, categoria);
    }

    public EstimativaCorrida estimarPorCoordenadas(Localizacao origem, Localizacao destino, CategoriaVeiculo categoria) {
        double distanciaKm = distanciaGeodesicaAproximada(origem, destino);
        return calcularEstimativa(distanciaKm, categoria);
    }

    // ===== helpers de estimativa =====

    private double estimarDistanciaHeuristica(String o, String d) {
        int h = Math.abs((o + "|" + d).hashCode());
        int delta = (h % 10);     // 0..9
        return 4.0 + delta;       // 4..13 km (heurístico)
    }

    private double distanciaGeodesicaAproximada(Localizacao o, Localizacao d) {
        double dx = o.latitude() - d.latitude();
        double dy = o.longitude() - d.longitude();
        return Math.sqrt(dx*dx + dy*dy) * 111.0; // ~km por grau
    }

    private EstimativaCorrida calcularEstimativa(double distanciaKm, CategoriaVeiculo cat) {
        int minutos = (int) Math.round(distanciaKm * 3.0);

        double base = 3.50, porKm = 1.20, porMin = 0.50; // UberX
        if (cat != null) {
            String nome = cat.name().toUpperCase();
            if (nome.contains("COMFORT")) { base = 4.50; porKm = 1.60; porMin = 0.60; }
            else if (nome.contains("BLACK")) { base = 7.00; porKm = 2.40; porMin = 0.80; }
            else if (nome.contains("BAG"))   { base*=1.10; porKm*=1.10; porMin*=1.10; }
            else if (nome.contains("XL"))    { base*=1.30; porKm*=1.30; porMin*=1.30; }
        }

        double preco = base + porKm * distanciaKm + porMin * minutos;
        preco = Math.round(preco * 100.0) / 100.0;
        return new EstimativaCorrida(distanciaKm, minutos, preco);
    }

    /**
     * ✅ NOVO MÉTODO: Concluir uma corrida (para permitir avaliação)
     */
    public void concluirCorrida(String corridaId, String usuarioEmail) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + corridaId);
        }

        // Verificar se o usuário tem permissão para concluir a corrida
        boolean isPassageiro = corrida.getEmailPassageiro().equals(usuarioEmail);
        boolean isMotorista = corrida.getMotoristaAlocado() != null && 
                              corrida.getMotoristaAlocado().equals(usuarioEmail);

        if (!isPassageiro && !isMotorista) {
            throw new IllegalArgumentException("Apenas o passageiro ou motorista da corrida podem concluí-la.");
        }

        if (corrida.getStatus() != CorridaStatus.EM_ANDAMENTO) {
            throw new IllegalArgumentException("Apenas corridas em andamento podem ser concluídas.");
        }

        corrida.setStatus(CorridaStatus.CONCLUIDA);
        repositorioCorrida.atualizar(corrida);

        System.out.println("✅ Corrida " + corridaId.substring(0, 8) + " concluída com sucesso!");
        System.out.println("📝 Agora você pode avaliar a corrida no menu 'Avaliar Corrida'.");
    }

    /**
     * ✅ NOVO MÉTODO: Obter corridas concluídas de um usuário
     */
    public List<Corrida> getCorridasConcluidas(String usuarioEmail) {
        List<Corrida> todasCorridas = repositorioCorrida.buscarTodas();
        List<Corrida> concluidas = new ArrayList<>();

        for (Corrida corrida : todasCorridas) {
            boolean isUsuarioDaCorrida = corrida.getEmailPassageiro().equals(usuarioEmail) ||
                                        (corrida.getMotoristaAlocado() != null && 
                                         corrida.getMotoristaAlocado().equals(usuarioEmail));
            
            if (isUsuarioDaCorrida && corrida.getStatus() == CorridaStatus.CONCLUIDA) {
                concluidas.add(corrida);
            }
        }

        return concluidas;
    }

    /**
     * ✅ NOVO MÉTODO: Cancelar uma corrida
     */
    public void cancelarCorrida(String corridaId, String usuarioEmail) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + corridaId);
        }

        if (!corrida.getEmailPassageiro().equals(usuarioEmail)) {
            throw new IllegalArgumentException("Apenas o passageiro da corrida pode cancelá-la.");
        }

        if (corrida.getStatus() != CorridaStatus.SOLICITADA && 
            corrida.getStatus() != CorridaStatus.EM_ANDAMENTO) {
            throw new IllegalArgumentException("Não é possível cancelar uma corrida " + corrida.getStatus());
        }

        corrida.setStatus(CorridaStatus.CANCELADA);
        repositorioCorrida.atualizar(corrida);

        System.out.println("❌ Corrida " + corridaId.substring(0, 8) + " cancelada.");
    }

    /**
     * ✅ NOVO MÉTODO: Obter corridas em andamento de um usuário
     */
    public List<Corrida> getCorridasEmAndamento(String usuarioEmail) {
        List<Corrida> todasCorridas = repositorioCorrida.buscarTodas();
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