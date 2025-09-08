package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class VisualizarCorridaComando implements Comando {

    @Override
    public String nome() { return "Visualizar Corrida (detalhes)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        var email = contexto.sessao.getUsuarioAtual().getEmail();
        List<Corrida> minhas = contexto.repositorioCorrida.buscarPorPassageiro(email);
        if (minhas.isEmpty()) { erro("Você ainda não tem corridas."); return; }

        System.out.println("Suas corridas:");
        for (int i = 0; i < minhas.size(); i++) {
            var c = minhas.get(i);
            System.out.printf("%d) %s  [orig: %s | dest: %s]  status=%s%n", i+1, c.getId(),
                    nvl(c.getOrigemEndereco()), nvl(c.getDestinoEndereco()), c.getStatus());
        }
        System.out.print("Escolha o número (Enter para última): ");
        String s = entrada.nextLine().trim();
        Corrida c = minhas.get(minhas.size() - 1);
        if (!s.isBlank()) {
            try {
                int idx = Integer.parseInt(s) - 1;
                if (idx < 0 || idx >= minhas.size()) { erro("Opção inválida."); return; }
                c = minhas.get(idx);
            } catch (NumberFormatException e) { erro("Digite um número válido."); return; }
        }

        System.out.println();
        System.out.println("ID: " + c.getId());
        System.out.println("Passageiro: " + email);
        System.out.println("Origem: " + nvl(c.getOrigemEndereco()));
        System.out.println("Destino: " + nvl(c.getDestinoEndereco()));
        if (c.getOrigem() == null || c.getDestino() == null) {
            System.out.println("(Sem coordenadas — mapa ASCII indisponível nesta corrida)");
        }
    }

    private String nvl(String s) { return (s == null || s.isBlank()) ? "(não informado)" : s; }
}
