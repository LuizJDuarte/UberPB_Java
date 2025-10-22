package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;
    private final EstimativaCorrida estimativaCorrida;
    private final ServicoLocalizacao servicoLocalizacao;

    public ServicoCorrida(RepositorioCorrida rc,
                          RepositorioUsuario ru,
                          EstimativaCorrida estimativaCorrida,
                          ServicoLocalizacao servicoLocalizacao) {
        this.repositorioCorrida = rc;
        this.repositorioUsuario = ru;
        this.estimativaCorrida = estimativaCorrida;
        this.servicoLocalizacao = servicoLocalizacao;
    }

    // CLI usa antes de confirmar a solicitação
    public EstimativaCorrida estimarPorEnderecos(String origem, String destino, CategoriaVeiculo cat) {
        return estimativaCorrida.calcularPorEnderecos(origem, destino, cat);
    }

    public Corrida solicitarCorrida(String emailPassageiro,
                                    String origemEndereco,
                                    String destinoEndereco,
                                    CategoriaVeiculo cat,
                                    MetodoPagamento mp) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origemEndereco == null || origemEndereco.isBlank() ||
            destinoEndereco == null || destinoEndereco.isBlank())
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        Corrida c = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim(), cat, mp);

        // Tenta coordenadas para habilitar mapa/progresso
        if (c.getOrigem() == null && c.getOrigemEndereco() != null) {
            try {
                Localizacao o = servicoLocalizacao.geocodificar(c.getOrigemEndereco());
                Localizacao d = servicoLocalizacao.geocodificar(c.getDestinoEndereco());
                c = Corrida.novaComCoordenadas(emailPassageiro, o, d, cat, mp);
            } catch (Exception ignored) {}
        }

        repositorioCorrida.salvar(c);
        return c;
    }

    // ===== Progresso "tempo real" =====

    // CORREÇÃO: Garantir que a corrida seja iniciada com valores realistas
    public void iniciarSeAceita(Corrida corrida, GerenciadorCorridasAtivas gerenciador) {
        if (corrida == null) {
            System.out.println("❌ Tentativa de iniciar corrida nula");
            return;
        }
        
        System.out.println("🔄 Verificando se pode iniciar corrida: " + corrida.getId() + 
                          " - Status: " + corrida.getStatus());
        
        if (corrida.getStatus() == CorridaStatus.ACEITA || corrida.getStatus() == CorridaStatus.EM_ANDAMENTO) {
            // Se já está ativa, não reiniciar
            if (gerenciador.isAtiva(corrida.getId())) {
                System.out.println("ℹ️  Corrida já está ativa no gerenciador");
                return;
            }
            
            var est = estimativa(corrida);
            System.out.println("📐 Estimativa da corrida: " + est.getMinutos() + " min, " + 
                             est.getDistanciaKm() + " km");
            
            // CORREÇÃO: Garantir valores mínimos
            int minutos = Math.max(5, est.getMinutos()); // Mínimo 5 minutos
            double distancia = Math.max(1.0, est.getDistanciaKm()); // Mínimo 1km
            
            gerenciador.iniciar(corrida.getId(), minutos, distancia);
            System.out.println("🚀 Corrida iniciada no gerenciador: " + minutos + " min, " + distancia + " km");
            
            // Atualizar status para EM_ANDAMENTO se estava apenas ACEITA
            if (corrida.getStatus() == CorridaStatus.ACEITA) {
                corrida.setStatus(CorridaStatus.EM_ANDAMENTO);
                repositorioCorrida.atualizar(corrida);
                System.out.println("📝 Status atualizado para EM_ANDAMENTO");
            }
        } else {
            System.out.println("⏸️  Corrida não pode ser iniciada - Status: " + corrida.getStatus());
        }
    }

    public com.uberpb.service.GerenciadorCorridasAtivas.Progresso progresso(String corridaId, GerenciadorCorridasAtivas gerenciador) {
        var progresso = gerenciador.progresso(corridaId);
        
        // CORREÇÃO: Se o progresso estiver concluído mas a corrida não foi encerrada
        if (progresso.concluida) {
            Corrida c = repositorioCorrida.buscarPorId(corridaId);
            if (c != null && c.getStatus() != CorridaStatus.CONCLUIDA) {
                c.setStatus(CorridaStatus.CONCLUIDA);
                repositorioCorrida.atualizar(c);
                gerenciador.encerrar(corridaId);
                System.out.println("🏁 Corrida concluída automaticamente: " + corridaId);
            }
        }
        
        return progresso;
    }

    public void encerrarSeConcluida(String corridaId, GerenciadorCorridasAtivas gerenciador) {
        if (gerenciador.isConcluida(corridaId)) {
            Corrida c = repositorioCorrida.buscarPorId(corridaId);
            if (c != null) {
                c.setStatus(CorridaStatus.CONCLUIDA);
                repositorioCorrida.atualizar(c);
            }
            gerenciador.encerrar(corridaId);
        }
    }

    // usado no ConcluirCorridaComando
    public void concluirCorrida(String corridaId, String emailSolicitante, GerenciadorCorridasAtivas gerenciador) {
        Corrida c = repositorioCorrida.buscarPorId(corridaId);
        if (c == null) throw new IllegalArgumentException("Corrida não encontrada.");

        boolean ehPass = emailSolicitante.equalsIgnoreCase(c.getEmailPassageiro());
        boolean ehMot  = emailSolicitante.equalsIgnoreCase(c.getMotoristaAlocado());
        if (!ehPass && !ehMot && !(repositorioUsuario.buscarPorEmail(emailSolicitante) instanceof Administrador)) {
            throw new SecurityException("Sem permissão para concluir esta corrida.");
        }

        gerenciador.encerrar(corridaId);
        c.setStatus(CorridaStatus.CONCLUIDA);
        repositorioCorrida.atualizar(c);
    }

    private EstimativaCorrida estimativa(Corrida c) {
        EstimativaCorrida est;
        
        if (c.getOrigem() != null && c.getDestino() != null) {
            est = estimativaCorrida.calcularPorCoordenadas(c.getOrigem(), c.getDestino(), c.getCategoriaEscolhida());
        } else {
            est = estimativaCorrida.calcularPorEnderecos(c.getOrigemEndereco(), c.getDestinoEndereco(), c.getCategoriaEscolhida());
        }
        
        // CORREÇÃO: Garantir valores mínimos de fallback
        if (est.getMinutos() <= 0 || est.getDistanciaKm() <= 0) {
            System.out.println("⚠️  Estimativa inválida, usando valores padrão");
            return new EstimativaCorrida(5.0, 10, 25.0); // 5km, 10min, R$25
        }
        
        return est;
    }
}