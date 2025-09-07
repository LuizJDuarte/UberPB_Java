package com.uberpb.app;

import com.uberpb.exceptions.CredenciaisInvalidasException;
import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.exceptions.UsuarioNaoEncontradoException;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoAutenticacao;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoValidacaoMotorista;
import com.uberpb.util.PasswordUtil;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AplicacaoUberPB {

    private static Scanner scanner = new Scanner(System.in);
    private static RepositorioUsuario repositorioUsuario = new ImplRepositorioUsuarioArquivo();
    private static ServicoCadastro servicoCadastro = new ServicoCadastro(repositorioUsuario);
    private static ServicoAutenticacao servicoAutenticacao = new ServicoAutenticacao(repositorioUsuario);
    private static ServicoValidacaoMotorista servicoValidacaoMotorista = new ServicoValidacaoMotorista(repositorioUsuario);

    private static Usuario usuarioLogado = null; // Armazena o usuário atualmente logado

    public static void main(String[] args) {
        exibirMenuPrincipal();
    }

    private static void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n--- Bem-vindo ao UberPB! ---");
            System.out.println("1. Entrar");
            System.out.println("2. Cadastrar");
            System.out.println("3. Listar Todos os Usuários (Admin - para demonstração)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha

                switch (opcao) {
                    case 1:
                        menuEntrar();
                        break;
                    case 2:
                        menuCadastrar();
                        break;
                    case 3:
                        listarTodosUsuarios();
                        break;
                    case 0:
                        System.out.println("Saindo do UberPB. Até mais!");
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine(); // Limpar o buffer do scanner
                opcao = -1; // Para manter o loop
            }
        } while (opcao != 0);
    }

    private static void menuEntrar() {
        System.out.println("\n--- Entrar ---");
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            usuarioLogado = servicoAutenticacao.autenticar(email, senha);
            System.out.println("Login realizado com sucesso! Bem-vindo, " + usuarioLogado.getEmail() + "!");

            if (usuarioLogado instanceof Passageiro) {
                menuPassageiro();
            } else if (usuarioLogado instanceof Motorista) {
                menuMotorista((Motorista) usuarioLogado);
            }
        } catch (UsuarioNaoEncontradoException | CredenciaisInvalidasException e) {
            System.out.println("Erro de login: " + e.getMessage());
        } finally {
            usuarioLogado = null; // Limpa o usuário logado ao sair do menu específico
        }
    }

    private static void menuCadastrar() {
        System.out.println("\n--- Cadastrar ---");
        System.out.println("Você deseja cadastrar como:");
        System.out.println("1. Passageiro");
        System.out.println("2. Motorista");
        System.out.print("Escolha uma opção: ");

        try {
            int tipo = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            System.out.print("Digite seu e-mail: ");
            String email = scanner.nextLine();
            System.out.print("Digite sua senha: ");
            String senha = scanner.nextLine();

            if (!PasswordUtil.isValidEmail(email)) {
                System.out.println("Erro: Formato de e-mail inválido.");
                return;
            }

            switch (tipo) {
                case 1:
                    servicoCadastro.cadastrarPassageiro(email, senha);
                    System.out.println("Passageiro cadastrado com sucesso!");
                    break;
                case 2:
                    servicoCadastro.cadastrarMotorista(email, senha);
                    System.out.println("Motorista cadastrado com sucesso! Lembre-se de validar seu veículo e documentos para ativar sua conta.");
                    break;
                default:
                    System.out.println("Opção de tipo de usuário inválida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.nextLine();
        } catch (EmailJaExistenteException e) {
            System.out.println("Erro de cadastro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de cadastro: " + e.getMessage());
        }
    }

    private static void menuPassageiro() {
        int opcao;
        do {
            System.out.println("\n--- Menu do Passageiro (" + usuarioLogado.getEmail() + ") ---");
            System.out.println("1. Solicitar Corrida (Não implementado)");
            System.out.println("2. Ver Histórico (Não implementado)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.println("Funcionalidade 'Solicitar Corrida' ainda não implementada.");
                        break;
                    case 2:
                        System.out.println("Funcionalidade 'Ver Histórico' ainda não implementada.");
                        break;
                    case 0:
                        System.out.println("Saindo do menu do Passageiro.");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }
        } while (opcao != 0);
    }

    private static void menuMotorista(Motorista motorista) {
        int opcao;
        do {
            System.out.println("\n--- Menu do Motorista (" + motorista.getEmail() + ") ---");
            System.out.println("Status da Conta: " + (motorista.isContaAtiva() ? "Ativa" : "Inativa - Valide seu veículo e documentos!"));
            System.out.println("1. Validar Veículo e Documentos (RF02)");
            System.out.println("2. Aceitar Corrida (Não implementado)");
            System.out.println("3. Ver Histórico (Não implementado)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        validarVeiculoDocumentosMotorista(motorista);
                        break;
                    case 2:
                        System.out.println("Funcionalidade 'Aceitar Corrida' ainda não implementada.");
                        break;
                    case 3:
                        System.out.println("Funcionalidade 'Ver Histórico' ainda não implementada.");
                        break;
                    case 0:
                        System.out.println("Saindo do menu do Motorista.");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }
            // Atualiza o objeto motorista na memória com o estado mais recente do repositório
            // para que o status da conta seja sempre exibido corretamente.
            motorista = (Motorista) repositorioUsuario.buscarPorEmail(motorista.getEmail());

        } while (opcao != 0);
    }

    private static void validarVeiculoDocumentosMotorista(Motorista motorista) {
        System.out.println("\n--- Validação de Veículo e Documentos ---");
        System.out.print("Modelo do veículo (ex: Toyota Corolla): ");
        String modelo = scanner.nextLine();
        System.out.print("Ano do veículo (ex: 2020): ");
        int ano = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Placa do veículo (ex: ABC-1234): ");
        String placa = scanner.nextLine();
        System.out.print("Cor do veículo: ");
        String cor = scanner.nextLine();
        System.out.print("Capacidade de passageiros (incluindo você): ");
        int capacidadePassageiros = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Tamanho do porta-malas (P, M, G - digite N/A se não souber ou não se aplica): ");
        String tamanhoPortaMalas = scanner.nextLine();

        Veiculo veiculo = new Veiculo(modelo, ano, placa, cor, capacidadePassageiros, tamanhoPortaMalas);

        System.out.print("Você possui CNH válida? (sim/não): ");
        boolean cnhValida = scanner.nextLine().equalsIgnoreCase("sim");
        System.out.print("Você possui CRLV (Certificado de Registro e Licenciamento de Veículo) válido? (sim/não): ");
        boolean crlvValido = scanner.nextLine().equalsIgnoreCase("sim");

        try {
            Motorista motoristaAtualizado = servicoValidacaoMotorista.validarVeiculoEDocumentos(motorista, veiculo, cnhValida, crlvValido);
            System.out.println("Validação concluída.");
            System.out.println("Status do motorista agora: " + motoristaAtualizado);
            // Atualiza o objeto logado para refletir as mudanças imediatamente
            usuarioLogado = motoristaAtualizado; 
        } catch (IllegalArgumentException e) {
            System.out.println("Erro na validação: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número para ano/capacidade.");
            scanner.nextLine(); // Limpar o buffer do scanner
        }
    }

    private static void listarTodosUsuarios() {
        System.out.println("\n--- Usuários Cadastrados ---");
        List<Usuario> todos = repositorioUsuario.buscarTodos();
        if (todos.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado ainda.");
        } else {
            for (Usuario u : todos) {
                System.out.println(u);
            }
        }
    }
}
