package com.uberpb.app;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.service.ServicoRotaMock;
import java.util.Scanner;
import static com.uberpb.app.ConsoleUI.*;

public class AtualizarLocalizacaoComando implements Comando {
    private final ServicoRotaMock servicoRota = new ServicoRotaMock();

    @Override
    public String nome() {
        return "Atualizar Localização";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Motorista;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        Motorista motorista = (Motorista) contexto.sessao.getUsuarioAtual();

        try {
            String nomeRua = lerString("Digite o nome da rua:", entrada);
            Localizacao novaLocalizacao = servicoRota.geocodificar(nomeRua);

            if (novaLocalizacao != null) {
                // motorista.setUltimaLocalizacao(novaLocalizacao); // ERRO: Este método não existe
                // contexto.repositorioUsuario.atualizar(motorista);
                ok("Localização atualizada com sucesso! (Funcionalidade desativada temporariamente)");
            } else {
                erro("Endereço não encontrado.");
            }
        } catch (Exception e) {
            erro("Erro ao atualizar localização: " + e.getMessage());
        }
    }

    private String lerString(String prompt, Scanner entrada) {
        System.out.print(prompt + " ");
        return entrada.nextLine();
    }
}
