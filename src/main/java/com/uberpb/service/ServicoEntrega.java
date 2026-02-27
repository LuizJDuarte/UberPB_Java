package com.uberpb.service;

import com.uberpb.model.Entregador;
import com.uberpb.model.Localizacao;
import com.uberpb.model.Pedido;
import com.uberpb.model.Restaurante;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioPedido;
import com.uberpb.repository.RepositorioUsuario;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar entregas e buscar entregadores (RF22)
 * Similar ao ServicoDirecionamentoCorrida mas para pedidos/entregas
 */
public class ServicoEntrega {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioPedido repositorioPedido;
    private final ServicoLocalizacao servicoLocalizacao;
    private final ServicoNotificacao servicoNotificacao;

    public ServicoEntrega(RepositorioUsuario repositorioUsuario,
            RepositorioPedido repositorioPedido,
            ServicoLocalizacao servicoLocalizacao,
            ServicoNotificacao servicoNotificacao) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPedido = repositorioPedido;
        this.servicoLocalizacao = servicoLocalizacao;
        this.servicoNotificacao = servicoNotificacao;
    }

    /**
     * RF22: Busca o entregador mais próximo do restaurante
     * 
     * @param emailRestaurante Email do restaurante de origem
     * @return Email do entregador mais próximo ou null se não houver disponível
     */
    public String buscarEntregadorMaisProximo(String emailRestaurante) {
        // Busca o restaurante
        Usuario usuarioRest = repositorioUsuario.buscarPorEmail(emailRestaurante);
        if (!(usuarioRest instanceof Restaurante restaurante)) {
            return null;
        }

        // Filtra entregadores disponíveis e ativos
        List<Entregador> entregadoresDisponiveis = repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Entregador)
                .map(u -> (Entregador) u)
                .filter(Entregador::isContaAtiva)
                .filter(Entregador::isDisponivel)
                .collect(Collectors.toList());

        if (entregadoresDisponiveis.isEmpty()) {
            return null;
        }

        // Localização do restaurante
        Localizacao locRestaurante = restaurante.getLocalizacao();

        // Encontra o mais próximo
        return entregadoresDisponiveis.stream()
                .map(e -> new ParEntregadorDistancia(
                        e.getEmail(),
                        servicoLocalizacao.distanciaKm(
                                servicoLocalizacao.obterLocalizacaoAtual(e.getEmail()),
                                locRestaurante)))
                .min(Comparator.comparingDouble(p -> p.distancia))
                .map(p -> p.emailEntregador)
                .orElse(null);
    }

    /**
     * RF22: Notifica restaurante e busca entregador para um pedido
     * 
     * @param pedido O pedido criado
     * @return true se entregador foi encontrado e notificado
     */
    public boolean processarNovoPedido(Pedido pedido) {
        // 1. Notifica o restaurante sobre novo pedido
        servicoNotificacao.notificarRestauranteNovoPedido(
                pedido.getEmailRestaurante(),
                pedido.getEmailCliente(),
                pedido.getTotal());

        // 2. Busca entregador mais próximo
        String emailEntregador = buscarEntregadorMaisProximo(pedido.getEmailRestaurante());

        if (emailEntregador == null) {
            System.out.println("⚠️ Nenhum entregador disponível no momento para o pedido.");
            return false;
        }

        // 3. Aloca entregador ao pedido
        pedido.setEntregadorAlocado(emailEntregador);

        // 4. Notifica o entregador sobre pedido disponível
        Usuario usuarioRest = repositorioUsuario.buscarPorEmail(pedido.getEmailRestaurante());
        String nomeRestaurante = usuarioRest instanceof Restaurante r ? r.getNomeFantasia()
                : pedido.getEmailRestaurante();

        servicoNotificacao.notificarEntregadorPedidoDisponivel(
                emailEntregador,
                nomeRestaurante,
                pedido.getTotal() * 0.15 // Assume 15% do valor como taxa de entrega
        );

        System.out.println("✅ Entregador " + emailEntregador + " alocado para o pedido.");
        return true;
    }

    /**
     * Lista entregadores disponíveis
     */
    public List<Entregador> listarEntregadoresDisponiveis() {
        return repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Entregador)
                .map(u -> (Entregador) u)
                .filter(Entregador::isDisponivel)
                .filter(Entregador::isContaAtiva)
                .collect(Collectors.toList());
    }

    /**
     * Classe auxiliar para calcular distância
     */
    private static class ParEntregadorDistancia {
        final String emailEntregador;
        final double distancia;

        ParEntregadorDistancia(String email, double dist) {
            this.emailEntregador = email;
            this.distancia = dist;
        }
    }
}
