package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.Motorista;
import com.uberpb.model.OfertaCorrida;
import com.uberpb.model.OfertaStatus;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

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
        corrida.setStatus(com.uberpb.model.CorridaStatus.ACEITA);
        repositorioCorrida.atualizar(corrida);

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

    private boolean motoristaAceitaCategoria(Motorista motorista, CategoriaVeiculo categoria) {
        try {
            Object veiculo = null;
            try {
                Method mGetVeiculo = motorista.getClass().getMethod("getVeiculo");
                veiculo = mGetVeiculo.invoke(motorista);
            } catch (NoSuchMethodException ignored) {}

            if (veiculo != null) {
                try {
                    Method mCats = veiculo.getClass().getMethod("getCategoriasDisponiveis");
                    Object res = mCats.invoke(veiculo);
                    if (res instanceof Collection<?> coll) {
                        for (Object o : coll) if (o != null && categoria.name().equalsIgnoreCase(o.toString())) return true;
                    }
                } catch (NoSuchMethodException ignored) {}
                try {
                    Method mCat = veiculo.getClass().getMethod("getCategoria");
                    Object res = mCat.invoke(veiculo);
                    if (res != null && categoria.name().equalsIgnoreCase(res.toString())) return true;
                } catch (NoSuchMethodException ignored) {}
            }

            try {
                Method mCats2 = motorista.getClass().getMethod("getCategorias");
                Object res = mCats2.invoke(motorista);
                if (res instanceof Collection<?> coll) {
                    for (Object o : coll) if (o != null && categoria.name().equalsIgnoreCase(o.toString())) return true;
                }
            } catch (NoSuchMethodException ignored) {}

            return true;
        } catch (Exception e) {
            return true;
        }
    }
}