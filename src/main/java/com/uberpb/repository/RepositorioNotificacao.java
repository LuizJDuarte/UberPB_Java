package com.uberpb.repository;

import com.uberpb.model.Notificacao;
import java.util.List;

/**
 * Repositório de Notificações (RF22)
 */
public interface RepositorioNotificacao {

    void salvar(Notificacao notificacao);

    List<Notificacao> buscarPorDestinatario(String email);

    List<Notificacao> buscarNaoLidasPorDestinatario(String email);

    List<Notificacao> listarTodas();

    Notificacao buscarPorId(String id);

    void atualizar(Notificacao notificacao);
}
