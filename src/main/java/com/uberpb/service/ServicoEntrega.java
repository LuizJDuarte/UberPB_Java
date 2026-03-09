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
     */
    public String buscarEntregadorMaisProximo(String emailRestaurante) {

        Usuario usuarioRest = repositorioUsuario.buscarPorEmail(emailRestaurante);

        if (!(usuarioRest instanceof Restaurante restaurante)) {
            return null;
        }

        List<Entregador> entregadoresDisponiveis = repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Entregador)
                .map(u -> (Entregador) u)
                .filter(Entregador::isContaAtiva)
                .filter(Entregador::isDisponivel)
                .collect(Collectors.toList());

        if (entregadoresDisponiveis.isEmpty()) {
            return null;
        }

        Localizacao locRestaurante = restaurante.getLocalizacao();

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
     * RF22: Quando cliente cria pedido
     * Agora apenas NOTIFICA o restaurante
     */
    public boolean processarNovoPedido(Pedido pedido) {

        servicoNotificacao.notificarRestauranteNovoPedido(
                pedido.getEmailRestaurante(),
                pedido.getEmailCliente(),
                pedido.getTotal());

        System.out.println("Restaurante notificado sobre novo pedido.");

        return true;
    }

    /**
     * NOVO MÉTODO
     * Chamado quando o restaurante CONFIRMA o pedido
     */
    public boolean buscarEntregadorParaPedido(Pedido pedido) {

        String emailEntregador = buscarEntregadorMaisProximo(pedido.getEmailRestaurante());

        if (emailEntregador == null) {
            System.out.println(" Nenhum entregador disponível no momento.");
            return false;
        }

        pedido.setEntregadorAlocado(emailEntregador);
        repositorioPedido.atualizar(pedido);

        Usuario usuarioRest = repositorioUsuario.buscarPorEmail(pedido.getEmailRestaurante());

        String nomeRestaurante = usuarioRest instanceof Restaurante r
                ? r.getNomeFantasia()
                : pedido.getEmailRestaurante();

        servicoNotificacao.notificarEntregadorPedidoDisponivel(
                emailEntregador,
                nomeRestaurante,
                pedido.getTotal());

        System.out.println("🚴 Entregador " + emailEntregador + " notificado sobre o pedido.");

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
     * RF24: Entregador aceita pedido
     */
    public boolean aceitarPedido(String emailEntregador, Pedido pedido) {

        if (pedido.getEntregadorAlocado() == null ||
                !pedido.getEntregadorAlocado().equalsIgnoreCase(emailEntregador)) {

            System.out.println("Este pedido não foi alocado para você.");
            return false;
        }

        if (!pedido.getStatus().equals("CONFIRMADO") &&
                !pedido.getStatus().equals("EM_PREPARO")) {

            System.out.println("Este pedido ainda não está disponível para entrega.");
            return false;
        }

        // NÃO muda o status do pedido
        // Apenas confirma que o entregador aceitou a entrega

        repositorioPedido.atualizar(pedido);

        servicoNotificacao.notificarCliente(
                pedido.getEmailCliente(),
                "Um entregador aceitou a entrega do seu pedido!");

        System.out.println("🚴 Pedido aceito para entrega!");
        return true;
    }

    /**
     * RF24: Entregador recusa pedido
     */
    public boolean recusarPedido(String emailEntregador, Pedido pedido) {

        if (pedido.getEntregadorAlocado() == null ||
                !pedido.getEntregadorAlocado().equalsIgnoreCase(emailEntregador)) {

            System.out.println("Este pedido não foi alocado para você.");
            return false;
        }

        if (!pedido.getStatus().equals("CONFIRMADO")) {
            System.out.println(" Este pedido não está disponível para recusa.");
            return false;
        }

        pedido.setEntregadorAlocado(null);
        repositorioPedido.atualizar(pedido);

        String novoEntregador = buscarEntregadorMaisProximo(pedido.getEmailRestaurante());

        if (novoEntregador != null && !novoEntregador.equalsIgnoreCase(emailEntregador)) {

            pedido.setEntregadorAlocado(novoEntregador);
            repositorioPedido.atualizar(pedido);

            Usuario usuarioRest = repositorioUsuario.buscarPorEmail(pedido.getEmailRestaurante());

            String nomeRestaurante = usuarioRest instanceof Restaurante r
                    ? r.getNomeFantasia()
                    : pedido.getEmailRestaurante();

            servicoNotificacao.notificarEntregadorPedidoDisponivel(
                    novoEntregador,
                    nomeRestaurante,
                    pedido.getTotal());

            System.out.println("Pedido enviado para outro entregador.");
        } else {

            System.out.println(" Nenhum outro entregador disponível.");
        }

        return true;
    }

    /**
     * Classe auxiliar para cálculo de distância
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