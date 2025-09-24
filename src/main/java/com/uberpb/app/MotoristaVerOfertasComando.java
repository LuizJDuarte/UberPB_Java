package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.OfertaCorrida;
import com.uberpb.model.OfertaStatus;
import com.uberpb.model.Usuario;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class MotoristaVerOfertasComando implements Comando {

    @Override public String nome() { return "Ver Ofertas de Corrida (motorista)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) { return usuarioAtualOuNull instanceof Motorista; }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String email = contexto.sessao.getUsuarioAtual().getEmail();
        List<OfertaCorrida> ofertas = contexto.servicoOferta.listarOfertasDoMotorista(email);

        if (ofertas.isEmpty()) { erro("Nenhuma oferta para você no momento."); return; }

        System.out.println("Suas ofertas:");
        int idx = 1;
        for (OfertaCorrida o : ofertas) {
            System.out.printf("%d) ID=%s | Corrida=%s | status=%s%n",
                    idx++, o.getId(), o.getCorridaId(), o.getStatus());
        }
        System.out.print("Escolha o número da oferta para responder (Enter para sair): ");
        String s = entrada.nextLine().trim();
        if (s.isBlank()) return;
        int escolha;
        try { escolha = Integer.parseInt(s) - 1; }
        catch (Exception e) { erro("Número inválido."); return; }
        if (escolha < 0 || escolha >= ofertas.size()) { erro("Índice inválido."); return; }
        OfertaCorrida selecionada = ofertas.get(escolha);
        if (selecionada.getStatus() != OfertaStatus.PENDENTE) { erro("Oferta já respondida."); return; }

        System.out.println("1) Aceitar   2) Recusar");
        System.out.print("> ");
        String ac = entrada.nextLine().trim();
        if ("1".equals(ac)) {
            contexto.servicoOferta.aceitarOferta(selecionada.getId(), email);
            ok("Você ACEITOU a oferta. A corrida foi alocada a você.");
        } else {
            contexto.servicoOferta.recusarOferta(selecionada.getId(), email);
            ok("Oferta recusada.");
        }
    }
}