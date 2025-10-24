
package com.uberpb.app;

import com.uberpb.model.Avaliacao;
import com.uberpb.model.AvaliacaoMotorista;
import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import java.util.Scanner;

public class AvaliarMotoristaComando implements Comando {

    @Override
    public String nome() {
        return "Avaliar Motorista";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        ConsoleUI.cabecalho("Avaliar Motorista", contexto.getSessao());

        System.out.print("ID da Corrida a ser avaliada: ");
        String corridaId = scanner.nextLine();

        System.out.print("Nota (1-5): ");
        int nota = Integer.parseInt(scanner.nextLine());

        System.out.print("Comentário: ");
        String comentario = scanner.nextLine();

        String passageiroEmail = contexto.getSessao().getUsuarioAtual().getEmail();

        contexto.getServicoAvaliacao().avaliarMotorista(corridaId, passageiroEmail, nota, comentario);

        ConsoleUI.ok("Avaliação enviada com sucesso!");
    }
}
