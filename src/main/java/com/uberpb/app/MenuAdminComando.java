
package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.TipoUsuario;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuAdminComando implements Comando {

    private final List<Comando> subComandos;

    public MenuAdminComando() {
        this.subComandos = inicializarSubComandos();
    }

    @Override
    public String nome() {
        return "Menu Administrativo";
    }

    @Override
    public boolean visivelPara(Usuario usuario) {
        return usuario != null && usuario.getTipo() == TipoUsuario.ADMIN;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner scanner) {
        boolean sair = false;
        while (!sair) {
            ConsoleUI.cabecalho("Menu Admin", contexto.getSessao());

            List<Comando> comandosVisiveis = subComandos.stream()
                .filter(c -> c.visivelPara(contexto.getSessao().getUsuarioAtual()))
                .collect(Collectors.toList());

            for (int i = 0; i < comandosVisiveis.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, comandosVisiveis.get(i).nome());
            }
            System.out.println("0) Voltar");

            System.out.print("> ");
            String opcao = scanner.nextLine().trim();

            if (opcao.equals("0")) {
                sair = true;
                continue;
            }

            try {
                int indice = Integer.parseInt(opcao) - 1;
                if (indice >= 0 && indice < comandosVisiveis.size()) {
                    comandosVisiveis.get(indice).executar(contexto, scanner);
                } else {
                    ConsoleUI.erro("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                ConsoleUI.erro("Entrada inválida. Por favor, insira um número.");
            }
        }
    }

    private List<Comando> inicializarSubComandos() {
        return List.of(
            // Comandos de Gestão de Motoristas
            new ListarMotoristasPendentesComando(),
            new AprovarDocumentosMotoristaComando(),
            new AprovarDocumentosEntregadorComando(),
            new AprovarDocumentosEntregadorComando(),

            // Comandos de Gestão Geral de Usuários
            new AtivarDesativarContaComando(),
            new AdminGerenciarUsuariosComando(), // Este já existe e permite listar/excluir

            // Comandos de Supervisão
            new AdminVisualizarCorridasComando(),
            new AdminModerarAvaliacoesComando()
        );
    }
}
