package com.uberpb.service;

import com.uberpb.model.Notificacao;
import com.uberpb.model.TipoNotificacao;
import com.uberpb.repository.RepositorioNotificacao;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de Notificações (RF22)
 * Gerencia o envio e consulta de notificações para usuários do sistema
 */
public class ServicoNotificacao {

    private final RepositorioNotificacao repositorio;

    public ServicoNotificacao(RepositorioNotificacao repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Envia uma notificação para um usuário
     * 
     * @param destinatarioEmail Email do destinatário
     * @param tipo              Tipo da notificação
     * @param mensagem          Mensagem da notificação
     * @return A notificação criada
     */
    public Notificacao enviarNotificacao(String destinatarioEmail, TipoNotificacao tipo, String mensagem) {
        String id = UUID.randomUUID().toString();
        Notificacao notificacao = new Notificacao(id, destinatarioEmail, tipo, mensagem);
        repositorio.salvar(notificacao);

        // Simula envio de notificação (em produção poderia ser email, SMS, push, etc.)
        System.out.println("📧 Notificação enviada para " + destinatarioEmail + ": " + mensagem);

        return notificacao;
    }

    /**
     * Notifica restaurante sobre novo pedido
     */
    public void notificarRestauranteNovoPedido(String emailRestaurante, String emailCliente, double total) {

        String mensagem = String.format(
                "Novo pedido recebido de %s no valor de R$ %.2f. Acesse para confirmar ou rejeitar.",
                emailCliente,
                total);

        Notificacao notificacao = new Notificacao(
                UUID.randomUUID().toString(),
                emailRestaurante,
                TipoNotificacao.NOVO_PEDIDO_RESTAURANTE,
                mensagem);

        repositorio.salvar(notificacao);
    }

    /**
     * Notifica entregador sobre pedido disponível
     */
    public Notificacao notificarEntregadorPedidoDisponivel(String emailEntregador, String emailRestaurante,
            double valorEntrega) {
        String mensagem = String.format(
                "Pedido disponível do restaurante %s! Valor da entrega: R$ %.2f",
                emailRestaurante,
                valorEntrega);
        return enviarNotificacao(emailEntregador, TipoNotificacao.PEDIDO_DISPONIVEL_ENTREGADOR, mensagem);
    }

    /**
     * RF24: Notifica cliente sobre evento genérico
     */
    public Notificacao notificarCliente(String emailCliente, String mensagem) {
        return enviarNotificacao(emailCliente, TipoNotificacao.SISTEMA, mensagem);
    }

    /**
     * Busca notificações não lidas de um usuário
     */
    public List<Notificacao> buscarNotificacoesNaoLidas(String email) {
        return repositorio.buscarNaoLidasPorDestinatario(email);
    }

    /**
     * Busca todas as notificações de um usuário
     */
    public List<Notificacao> buscarNotificacoes(String email) {
        return repositorio.buscarPorDestinatario(email);
    }

    /**
     * Marca uma notificação como lida
     */
    public void marcarComoLida(String notificacaoId) {
        Notificacao notificacao = repositorio.buscarPorId(notificacaoId);
        if (notificacao != null) {
            notificacao.marcarComoLida();
            repositorio.atualizar(notificacao);
        }
    }

    /**
     * Conta notificações não lidas
     */
    public int contarNotificacoesNaoLidas(String email) {
        return buscarNotificacoesNaoLidas(email).size();
    }
}
