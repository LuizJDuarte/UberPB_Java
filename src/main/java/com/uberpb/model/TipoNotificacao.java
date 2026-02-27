package com.uberpb.model;

/**
 * Tipos de notificações para o sistema (RF22)
 */
public enum TipoNotificacao {
    NOVO_PEDIDO_RESTAURANTE, // Notificação para restaurante sobre novo pedido
    PEDIDO_DISPONIVEL_ENTREGADOR, // Notificação para entregador sobre pedido disponível
    PEDIDO_ACEITO, // Pedido foi aceito por entregador
    PEDIDO_EM_ROTA, // Entregador está a caminho
    PEDIDO_ENTREGUE, // Pedido foi entregue
    PEDIDO_CANCELADO, // Pedido foi cancelado
    CORRIDA_DISPONIVEL, // Corrida disponível para motorista
    CORRIDA_CONFIRMADA, // Corrida foi confirmada
    SISTEMA // Notificação geral do sistema
}
