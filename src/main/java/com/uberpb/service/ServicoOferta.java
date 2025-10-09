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
import java.util.stream.Collectors;

public class ServicoOferta {

    private final RepositorioOferta repositorioOferta;
    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCorrida repositorioCorrida;

    // Necessários para RF09 (mais próximo)
    private final ServicoDirecionamentoCorrida servicoDirecionamentoCorrida;
    private final ServicoLocalizacao servicoLocalizacao;

    /** Construtor completo (preferencial). */
    public ServicoOferta(RepositorioOferta rOferta,
                         RepositorioUsuario rUsuario,
                         RepositorioCorrida rCorrida,
                         ServicoDirecionamentoCorrida sDir,
                         ServicoLocalizacao sLoc) {
        this.repositorioOferta  = rOferta;
        this.repositorioUsuario = rUsuario;
        this.repositorioCorrida = rCorrida;
        this.servicoDirecionamentoCorrida = sDir;
        this.servicoLocalizacao = sLoc;
    }

    /** Construtor de compatibilidade (se o provedor ainda não injeta os serviços). */
    public ServicoOferta(RepositorioOferta rOferta,
                         RepositorioUsuario rUsuario,
                         RepositorioCorrida rCorrida) {
        this(rOferta, rUsuario, rCorrida,
                new ServicoDirecionamentoCorrida(rUsuario, rCorrida),
                new ServicoLocalizacao());
    }

    /** RF07+RF08+RF09: cria UMA oferta para o motorista mais próximo da categoria. */
    public int criarOfertasParaCorrida(Corrida corrida) {
        // candidatos por categoria
        List<Motorista> candidatos = servicoDirecionamentoCorrida.filtrarCandidatos(corrida);
        if (candidatos.isEmpty()) return 0;

        // escolhe 1 mais próximo (origem da corrida)
        String emailMotorista = servicoDirecionamentoCorrida
                .escolherMotoristaMaisProximo(corrida, candidatos, servicoLocalizacao);
        if (emailMotorista == null) return 0;

        // cria UMA oferta
        OfertaCorrida oferta = OfertaCorrida.nova(corrida.getId(), emailMotorista);
        repositorioOferta.salvar(oferta);
        return 1;
    }

    // ==== Fluxo de aceitar/recusar oferta ====

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

        // Recusar automaticamente outras ofertas pendentes (se houver)
        List<OfertaCorrida> outras = repositorioOferta.buscarPorCorrida(corrida.getId());
        for (OfertaCorrida o : outras) {
            if (!o.getId().equals(ofertaId) && o.getStatus() == OfertaStatus.PENDENTE) {
                o.setStatus(OfertaStatus.RECUSADA);
                repositorioOferta.atualizar(o);
            }
        }
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
    }

    // ==== utilitários que você já usava ====

    public List<OfertaCorrida> buscarOfertasPorCorrida(String corridaId) {
        return repositorioOferta.buscarPorCorrida(corridaId);
    }

    public boolean corridaTemMotoristaAlocado(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        return corrida != null && corrida.getMotoristaAlocado() != null;
    }

    public Optional<Motorista> obterMotoristaAlocado(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null || corrida.getMotoristaAlocado() == null) return Optional.empty();
        Usuario u = repositorioUsuario.buscarPorEmail(corrida.getMotoristaAlocado());
        if (u instanceof Motorista m) return Optional.of(m);
        return Optional.empty();
    }

    public void cancelarOfertasDaCorrida(String corridaId) {
        List<OfertaCorrida> ofertas = repositorioOferta.buscarPorCorrida(corridaId);
        for (OfertaCorrida o : ofertas) {
            if (o.getStatus() == OfertaStatus.PENDENTE) {
                o.setStatus(OfertaStatus.RECUSADA);
                repositorioOferta.atualizar(o);
            }
        }
    }

    private boolean motoristaAceitaCategoria(Motorista m, CategoriaVeiculo categoria) {
        if (categoria == null) return true;
        try {
            Veiculo v = m.getVeiculo();
            return v != null && v.getCategoriasDisponiveis() != null
                    && v.getCategoriasDisponiveis().contains(categoria);
        } catch (Exception e) {
            return false;
        }
    }
}
