package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Usuario;
import java.util.Optional;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class AtualizarCorridaComando implements Comando {
    @Override
    public String nome() {
        return "Atualizar Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull != null;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String usuarioEmail = contexto.sessao.getUsuarioAtual().getEmail();
        
        // Encontrar a corrida a ser atualizada (a mais recente que ainda não começou)
        Optional<Corrida> corridaParaAtualizar = contexto.repositorioCorrida.buscarPorPassageiro(usuarioEmail)
            .stream()
            .filter(c -> c.getStatus() == CorridaStatus.SOLICITADA)
            .findFirst();

        if (corridaParaAtualizar.isEmpty()) {
            ok("Nenhuma corrida solicitada para atualizar.");
            return;
        }
        
        Corrida corrida = corridaParaAtualizar.get();

        System.out.print("Novo endereço de origem (atual: " + corrida.getOrigemEndereco() + "): ");
        String novoEnderecoOrigem = entrada.nextLine();
        if (novoEnderecoOrigem.isBlank()) {
            novoEnderecoOrigem = corrida.getOrigemEndereco();
        }

        System.out.print("Novo endereço de destino (atual: " + corrida.getDestinoEndereco() + "): ");
        String novoEnderecoDestino = entrada.nextLine();
        if (novoEnderecoDestino.isBlank()) {
            novoEnderecoDestino = corrida.getDestinoEndereco();
        }

        System.out.print("Nova categoria (atual: " + corrida.getCategoriaEscolhida() + "): ");
        String novaCategoriaStr = entrada.nextLine();
        CategoriaVeiculo novaCategoria = corrida.getCategoriaEscolhida();
        if (!novaCategoriaStr.isBlank()) {
            try {
                novaCategoria = CategoriaVeiculo.fromString(novaCategoriaStr);
            } catch (IllegalArgumentException e) {
                erro("Categoria inválida. A categoria não foi alterada.");
            }
        }

        Corrida novaCorrida = new Corrida(
            corrida.getId(),
            corrida.getEmailPassageiro(),
            novoEnderecoOrigem,
            novoEnderecoDestino,
            corrida.getOrigem(),
            corrida.getDestino(),
            novaCategoria,
            corrida.getMetodoPagamento(),
            corrida.getMotoristaAlocado(),
            corrida.getStatus()
        );

        contexto.repositorioCorrida.atualizar(novaCorrida);
        // Recria as ofertas para a corrida com os novos dados
        contexto.servicoOferta.criarOfertasParaCorrida(corrida);

        ok("Corrida atualizada com sucesso! Novas ofertas foram enviadas aos motoristas.");
    }
}
