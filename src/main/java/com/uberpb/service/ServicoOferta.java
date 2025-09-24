package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Motorista;
import com.uberpb.model.OfertaCorrida;
import com.uberpb.model.OfertaStatus;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import java.util.List;
import java.util.Optional;

public class ServicoOferta {

    private final RepositorioOferta repositorioOferta;
    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCorrida repositorioCorrida;

    public ServicoOferta(RepositorioOferta repositorioOferta,
                         RepositorioUsuario repositorioUsuario,
                         RepositorioCorrida repositorioCorrida) {
        this.repositorioOferta = repositorioOferta;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCorrida = repositorioCorrida;
    }

    public int criarOfertasParaCorrida(Corrida corrida) {
        CategoriaVeiculo categoria = corrida.getCategoriaEscolhida();
        int criadas = 0;
        List<Usuario> todos = repositorioUsuario.buscarTodos();
        for (Usuario u : todos) {
            if (u instanceof Motorista m) {
                try { if (!m.isContaAtiva()) continue; } catch (Exception ignored) {}
                if (motoristaAceitaCategoria(m, categoria)) {
                    var oferta = OfertaCorrida.nova(corrida.getId(), m.getEmail());
                    repositorioOferta.salvar(oferta);
                    criadas++;
                }
            }
        }
        return criadas;
    }

    /**
     * NOVO MÉTODO: Cria e direciona automaticamente uma oferta para um motorista disponível
     * Implementa o direcionamento automático da corrida
     */
    public Optional<OfertaCorrida> criarEDirecionarOferta(Corrida corrida) {
        CategoriaVeiculo categoria = corrida.getCategoriaEscolhida();
        
        // Buscar motoristas disponíveis que aceitam a categoria
        List<Motorista> motoristasDisponiveis = repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Motorista)
                .map(u -> (Motorista) u)
                .filter(m -> m.isContaAtiva() && 
                             m.isCnhValida() && 
                             m.isCrlvValido() &&
                             motoristaAceitaCategoria(m, categoria))
                .toList();

        if (motoristasDisponiveis.isEmpty()) {
            return Optional.empty();
        }

        // Escolher o primeiro motorista disponível (em sistema real, seria por proximidade)
        Motorista motorista = motoristasDisponiveis.get(0);
        
        // Criar oferta já aceita automaticamente
        OfertaCorrida oferta = OfertaCorrida.nova(corrida.getId(), motorista.getEmail());
        oferta.setStatus(OfertaStatus.ACEITA);
        repositorioOferta.salvar(oferta);

        // Atualizar corrida com motorista alocado
        corrida.setMotoristaAlocado(motorista.getEmail());
        corrida.setStatus(CorridaStatus.EM_ANDAMENTO);
        repositorioCorrida.atualizar(corrida);

        System.out.println("✅ Corrida direcionada automaticamente para: " + motorista.getEmail());

        return Optional.of(oferta);
    }

    /**
     * NOVO MÉTODO: Direcionamento automático para uma corrida específica
     * Pode ser chamado separadamente se necessário
     */
    public boolean direcionarCorridaAutomaticamente(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + corridaId);
        }

        // Verificar se já tem motorista alocado
        if (corrida.getMotoristaAlocado() != null) {
            System.out.println("Corrida já possui motorista alocado: " + corrida.getMotoristaAlocado());
            return true;
        }

        Optional<OfertaCorrida> oferta = criarEDirecionarOferta(corrida);
        return oferta.isPresent();
    }

    public List<OfertaCorrida> listarOfertasDoMotorista(String motoristaEmail) {
        return repositorioOferta.buscarPorMotorista(motoristaEmail);
    }

    public void aceitarOferta(String ofertaId, String motoristaEmail) {
        OfertaCorrida oferta = repositorioOferta.buscarPorId(ofertaId);
        if (oferta == null) throw new IllegalArgumentException("Oferta não encontrada.");
        if (!oferta.getMotoristaEmail().equalsIgnoreCase(motoristaEmail))
            throw new IllegalArgumentException("Oferta não pertence ao motorista.");
        if (oferta.getStatus() != OfertaStatus.PENDENTE)
            throw new IllegalArgumentException("Oferta já foi respondida.");

        oferta.setStatus(OfertaStatus.ACEITA);
        repositorioOferta.atualizar(oferta);

        Corrida corrida = repositorioCorrida.buscarPorId(oferta.getCorridaId());
        if (corrida == null) throw new IllegalStateException("Corrida da oferta não encontrada.");
        corrida.setMotoristaAlocado(motoristaEmail);
        corrida.setStatus(CorridaStatus.EM_ANDAMENTO);
        repositorioCorrida.atualizar(corrida);

        // Recusar automaticamente outras ofertas pendentes para a mesma corrida
        List<OfertaCorrida> outras = repositorioOferta.buscarPorCorrida(corrida.getId());
        for (OfertaCorrida o : outras) {
            if (!o.getId().equals(ofertaId) && o.getStatus() == OfertaStatus.PENDENTE) {
                o.setStatus(OfertaStatus.RECUSADA);
                repositorioOferta.atualizar(o);
            }
        }

        System.out.println("✅ Motorista " + motoristaEmail + " aceitou a corrida " + corrida.getId());
    }

    public void recusarOferta(String ofertaId, String motoristaEmail) {
        OfertaCorrida oferta = repositorioOferta.buscarPorId(ofertaId);
        if (oferta == null) throw new IllegalArgumentException("Oferta não encontrada.");
        if (!oferta.getMotoristaEmail().equalsIgnoreCase(motoristaEmail))
            throw new IllegalArgumentException("Oferta não pertence ao motorista.");
        if (oferta.getStatus() != OfertaStatus.PENDENTE)
            throw new IllegalArgumentException("Oferta já foi respondida.");
        
        oferta.setStatus(OfertaStatus.RECUSADA);
        repositorioOferta.atualizar(oferta);
        
        System.out.println("❌ Motorista " + motoristaEmail + " recusou a oferta " + ofertaId);
    }

    /**
     * NOVO MÉTODO: Buscar ofertas por corrida
     */
    public List<OfertaCorrida> buscarOfertasPorCorrida(String corridaId) {
        return repositorioOferta.buscarPorCorrida(corridaId);
    }

    /**
     * NOVO MÉTODO: Verificar se uma corrida já tem motorista alocado
     */
    public boolean corridaTemMotoristaAlocado(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        return corrida != null && corrida.getMotoristaAlocado() != null;
    }

    /**
     * NOVO MÉTODO: Obter motorista alocado para uma corrida
     */
    public Optional<Motorista> obterMotoristaAlocado(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null || corrida.getMotoristaAlocado() == null) {
            return Optional.empty();
        }
        
        Usuario usuario = repositorioUsuario.buscarPorEmail(corrida.getMotoristaAlocado());
        if (usuario instanceof Motorista motorista) {
            return Optional.of(motorista);
        }
        
        return Optional.empty();
    }

    /**
     * MÉTODO MELHORADO: Verificação simplificada de categoria do motorista
     */
    private boolean motoristaAceitaCategoria(Motorista motorista, CategoriaVeiculo categoria) {
        if (categoria == null) {
            // Se não há categoria específica, qualquer motorista serve
            return true;
        }

        try {
            Veiculo veiculo = motorista.getVeiculo();
            if (veiculo == null) {
                return false; // Motorista sem veículo não aceita nenhuma categoria
            }

            // Verificar se o veículo tem a categoria necessária
            List<CategoriaVeiculo> categoriasDisponiveis = veiculo.getCategoriasDisponiveis();
            if (categoriasDisponiveis != null) {
                return categoriasDisponiveis.contains(categoria);
            }

            return false;
            
        } catch (Exception e) {
            System.err.println("Erro ao verificar categoria do motorista: " + e.getMessage());
            return false;
        }
    }

    /**
     * NOVO MÉTODO: Cancelar todas as ofertas de uma corrida
     * Útil quando a corrida é cancelada pelo passageiro
     */
    public void cancelarOfertasDaCorrida(String corridaId) {
        List<OfertaCorrida> ofertas = repositorioOferta.buscarPorCorrida(corridaId);
        for (OfertaCorrida oferta : ofertas) {
            if (oferta.getStatus() == OfertaStatus.PENDENTE) {
                oferta.setStatus(OfertaStatus.RECUSADA);
                repositorioOferta.atualizar(oferta);
            }
        }
        System.out.println("📋 " + ofertas.size() + " ofertas canceladas para corrida " + corridaId);
    }

    /**
     * NOVO MÉTODO: Estatísticas das ofertas
     */
    public void exibirEstatisticasOfertas(String corridaId) {
        List<OfertaCorrida> ofertas = repositorioOferta.buscarPorCorrida(corridaId);
        
        long pendentes = ofertas.stream().filter(o -> o.getStatus() == OfertaStatus.PENDENTE).count();
        long aceitas = ofertas.stream().filter(o -> o.getStatus() == OfertaStatus.ACEITA).count();
        long recusadas = ofertas.stream().filter(o -> o.getStatus() == OfertaStatus.RECUSADA).count();
        
        System.out.println("📊 Estatísticas da corrida " + corridaId.substring(0, 8) + ":");
        System.out.println("   • Pendentes: " + pendentes);
        System.out.println("   • Aceitas: " + aceitas);
        System.out.println("   • Recusadas: " + recusadas);
        System.out.println("   • Total: " + ofertas.size());
    }
}