package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.*;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioOfertaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.*;
import java.util.ArrayList;
import java.util.List;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    // ===== REPOSITÓRIOS =====
    private static RepositorioUsuario repoUsuario() { 
        return new ImplRepositorioUsuarioArquivo(); 
    }
    
    private static RepositorioCorrida repoCorrida() { 
        return new ImplRepositorioCorridaArquivo(); 
    }
    
    private static RepositorioOferta repoOferta() { 
        return new ImplRepositorioOfertaArquivo(); 
    }

    // ===== SERVIÇOS EXISTENTES =====
    private static ServicoCadastro servicoCadastro(RepositorioUsuario rUsuario) { 
        return new ServicoCadastro(rUsuario); 
    }
    
    private static ServicoAutenticacao servicoAutenticacao(RepositorioUsuario rUsuario) { 
        return new ServicoAutenticacao(rUsuario); 
    }
    
    private static ServicoCorrida servicoCorrida(RepositorioCorrida rCorrida, RepositorioUsuario rUsuario) { 
        return new ServicoCorrida(rCorrida, rUsuario); 
    }
    
    private static ServicoOferta servicoOferta(RepositorioOferta rOferta, RepositorioUsuario rUsuario, RepositorioCorrida rCorrida) { 
        return new ServicoOferta(rOferta, rUsuario, rCorrida); 
    }
    
    private static ServicoValidacaoMotorista servicoValidacaoMotorista(RepositorioUsuario rUsuario) { 
        return new ServicoValidacaoMotorista(rUsuario); 
    }

    // ===== SERVIÇOS DE DIRECIONAMENTO E LOCALIZAÇÃO =====
    private static ServicoDirecionamentoCorrida servicoDirecionamentoCorrida(
            RepositorioCorrida rCorrida, RepositorioUsuario rUsuario) {
        return new ServicoDirecionamentoCorrida(rCorrida, rUsuario);
    }
    
    private static ServicoLocalizacao servicoLocalizacao(RepositorioCorrida rCorrida) {
        return new ServicoLocalizacao(rCorrida);
    }

    // ===== NOVO SERVIÇO DE OTIMIZAÇÃO DE ROTA =====
    private static ServicoOtimizacaoRota servicoOtimizacaoRota() {
        return new ServicoOtimizacaoRota();
    }

    // ===== CONTEXTO PRINCIPAL =====
    public static ContextoAplicacao fornecerContexto() {
        // Inicializar repositórios
        RepositorioUsuario rUsuario = repoUsuario();
        RepositorioCorrida rCorrida = repoCorrida();
        RepositorioOferta rOferta = repoOferta();

        // Inicializar serviços existentes
        ServicoCadastro sCadastro = servicoCadastro(rUsuario);
        ServicoAutenticacao sAuth = servicoAutenticacao(rUsuario);
        ServicoCorrida sCorrida = servicoCorrida(rCorrida, rUsuario);
        ServicoOferta sOferta = servicoOferta(rOferta, rUsuario, rCorrida);
        ServicoValidacaoMotorista sValidacao = servicoValidacaoMotorista(rUsuario);

        // Inicializar serviços de direcionamento e localização
        ServicoDirecionamentoCorrida sDirecionamento = servicoDirecionamentoCorrida(rCorrida, rUsuario);
        ServicoLocalizacao sLocalizacao = servicoLocalizacao(rCorrida);

        // INICIALIZAR NOVO SERVIÇO DE OTIMIZAÇÃO
        ServicoOtimizacaoRota sOtimizacao = servicoOtimizacaoRota();

        Sessao sessao = new Sessao();

        return new ContextoAplicacao(
            sessao, 
            rUsuario, 
            sCadastro, 
            sAuth, 
            rCorrida, 
            sCorrida, 
            rOferta, 
            sOferta, 
            sValidacao,
            sDirecionamento,
            sLocalizacao,
            sOtimizacao  // NOVO SERVIÇO
        );
    }

    // ===== COMANDOS DO MENU =====
    public static List<Comando> fornecerComandosPadrao() {
        List<Comando> comandos = new ArrayList<>();

        // Comandos básicos de autenticação
        comandos.add(new LoginComando());
        comandos.add(new CadastrarPassageiroComando());
        comandos.add(new CadastrarMotoristaComando());
        
        // Comandos de corrida (passageiro)
        comandos.add(new SolicitarCorridaComando());
        comandos.add(new VisualizarCorridaComando());
        comandos.add(new AcompanharCorridaComando()); // COMANDO MELHORADO
        
        // Comandos de motorista
        comandos.add(new MotoristaVerOfertasComando());
        comandos.add(new CompletarCadastroMotoristaComando());

        // NOVO COMANDO: Otimizar Rota
        comandos.add(new OtimizarRotaComando());

        // Comando funcional: Listar Usuários
        comandos.add(new ComandoFuncional(
                "Listar Usuários", 
                u -> u != null, // visível apenas para logados
                (ctx, in) -> {
                    var lista = ctx.repositorioUsuario.buscarTodos();
                    if (lista.isEmpty()) { 
                        System.out.println("(sem usuários cadastrados)"); 
                        return; 
                    }
                    for (Usuario u2 : lista) {
                        if (u2 instanceof Motorista m) {
                            String status = m.isContaAtiva() ? "Ativo" : "Inativo";
                            String cats = (m.getVeiculo() != null && m.getVeiculo().getCategoriasDisponiveis() != null)
                                    ? m.getVeiculo().getCategoriasDisponiveis().toString()
                                    : "[]";
                            System.out.println("[MOTORISTA] " + m.getEmail()
                                    + " | CNH: " + m.isCnhValida()
                                    + " | CRLV: " + m.isCrlvValido()
                                    + " | " + status
                                    + " | Categorias: " + cats);
                        } else if (u2 instanceof Passageiro) {
                            System.out.println("[PASSAGEIRO] " + u2.getEmail());
                        } else {
                            System.out.println("[USUARIO] " + u2.getEmail());
                        }
                    }
                }
        ));

        // Comando funcional: Logout
        comandos.add(new ComandoFuncional(
                "Logout", 
                u -> u != null, // visível apenas para logados
                (ctx, in) -> { 
                    ctx.sessao.deslogar(); 
                    ok("Sessão encerrada."); 
                }
        ));

        // Comando para testar direcionamento automático (apenas para desenvolvimento)
        comandos.add(new ComandoFuncional(
                "[DEV] Testar Direcionamento", 
                u -> u instanceof Passageiro, // apenas passageiros
                (ctx, in) -> {
                    try {
                        System.out.print("ID da corrida para direcionar: ");
                        String corridaId = in.nextLine().trim();
                        
                        boolean sucesso = ctx.servicoOferta.direcionarCorridaAutomaticamente(corridaId);
                        if (sucesso) {
                            ok("Corrida direcionada com sucesso!");
                        } else {
                            erro("Nenhum motorista disponível para esta corrida.");
                        }
                    } catch (Exception e) {
                        erro("Erro ao direcionar corrida: " + e.getMessage());
                    }
                }
        ));

        return comandos;
    }
}