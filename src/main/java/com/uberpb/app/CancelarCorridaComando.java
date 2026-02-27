package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Usuario;
import java.util.List;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class CancelarCorridaComando implements Comando {
    @Override
    public String nome() {
        return "Cancelar Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull != null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        List<Corrida> corridas = contexto.repositorioCorrida.buscarPorPassageiro(contexto.sessao.getUsuarioAtual().getEmail());
        Corrida corridaEmAndamento = null;
        for (Corrida c : corridas) {
            if (c.getStatus() != CorridaStatus.CONCLUIDA && c.getStatus() != CorridaStatus.CANCELADA) {
                corridaEmAndamento = c;
                break;
            }
        }

        if (corridaEmAndamento == null) {
            ok("Nenhuma corrida em andamento para cancelar.");
            return;
        }

        // A verificação anterior foi removida para permitir o cancelamento em qualquer estado não finalizado.

        corridaEmAndamento.setStatus(CorridaStatus.CANCELADA);
        contexto.repositorioCorrida.atualizar(corridaEmAndamento);
        ok("Corrida cancelada com sucesso!");
    }
}
