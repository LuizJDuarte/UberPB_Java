package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.*;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.ImplRepositorioAvaliacaoArquivo;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioOfertaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioAvaliacao;
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

    // ✅ NOVO REPOSITÓRIO DE AVALIAÇÕES
    private static RepositorioAvaliacao repoAvaliacao() { 
        return new ImplRepositorioAvaliacaoArquivo(); 
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

    // ===== SERVIÇO DE OTIMIZAÇÃO DE ROTA =====
    private static ServicoOtimizacaoRota servicoOtimizacaoRota() {
        return new ServicoOtimizacaoRota();
    }

    // ✅ NOVO SERVIÇO DE AVALIAÇÃO
    private static ServicoAvaliacao servicoAvaliacao(RepositorioAvaliacao rAvaliacao, 
                                                    RepositorioCorrida rCorrida,
                                                    RepositorioUsuario rUsuario) {
        return new ServicoAvaliacao(rAvaliacao, rCorrida, rUsuario);
    }

    // ===== CONTEXTO PRINCIPAL =====
    public static ContextoAplicacao fornecerContexto() {
        // Inicializar repositórios
        RepositorioUsuario rUsuario = repoUsuario();
        RepositorioCorrida rCorrida = repoCorrida();
        RepositorioOferta rOferta = repoOferta();
        RepositorioAvaliacao rAvaliacao = repoAvaliacao(); // ✅ NOVO REPOSITÓRIO

        // Inicializar serviços existentes
        ServicoCadastro sCadastro = servicoCadastro(rUsuario);
        ServicoAutenticacao sAuth = servicoAutenticacao(rUsuario);
        ServicoCorrida sCorrida = servicoCorrida(rCorrida, rUsuario);
        ServicoOferta sOferta = servicoOferta(rOferta, rUsuario, rCorrida);
        ServicoValidacaoMotorista sValidacao = servicoValidacaoMotorista(rUsuario);

        // Inicializar serviços de direcionamento e localização
        ServicoDirecionamentoCorrida sDirecionamento = servicoDirecionamentoCorrida(rCorrida, rUsuario);
        ServicoLocalizacao sLocalizacao = servicoLocalizacao(rCorrida);

        // Inicializar serviço de otimização
        ServicoOtimizacaoRota sOtimizacao = servicoOtimizacaoRota();

        // ✅ INICIALIZAR NOVO SERVIÇO DE AVALIAÇÃO
        ServicoAvaliacao sAvaliacao = servicoAvaliacao(rAvaliacao, rCorrida, rUsuario);

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
            sOtimizacao,
            sAvaliacao  // ✅ NOVO SERVIÇO
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
        comandos.add(new AcompanharCorridaComando());
        comandos.add(new ConcluirCorridaComando()); // ✅ NOVO COMANDO
        
        // Comandos de motorista
        comandos.add(new MotoristaVerOfertasComando());
        comandos.add(new CompletarCadastroMotoristaComando());

        // Comando de otimização de rota
        comandos.add(new OtimizarRotaComando());

        // ✅ NOVOS COMANDOS DE AVALIAÇÃO
        comandos.add(new AvaliarCorridaComando());
        comandos.add(new VisualizarAvaliacoesComando());

        // Comando funcional: Listar Usuários
        comandos.add(new ComandoFuncional(
                "Listar Usuários", 
                u -> u != null,
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
                            String rating = m.getTotalAvaliacoes() > 0 ? 
                                String.format("⭐ %.1f (%d)", m.getRatingMedio(), m.getTotalAvaliacoes()) : 
                                "⭐ Sem avaliações";
                            System.out.println("[MOTORISTA] " + m.getEmail()
                                    + " | CNH: " + m.isCnhValida()
                                    + " | CRLV: " + m.isCrlvValido()
                                    + " | " + status
                                    + " | " + rating
                                    + " | Categorias: " + cats);
                        } else if (u2 instanceof Passageiro p) {
                            String rating = p.getTotalAvaliacoes() > 0 ? 
                                String.format("⭐ %.1f (%d)", p.getRatingMedio(), p.getTotalAvaliacoes()) : 
                                "⭐ Sem avaliações";
                            System.out.println("[PASSAGEIRO] " + p.getEmail() + " | " + rating);
                        } else {
                            System.out.println("[USUARIO] " + u2.getEmail());
                        }
                    }
                }
        ));

        // Comando funcional: Logout
        comandos.add(new ComandoFuncional(
                "Logout", 
                u -> u != null,
                (ctx, in) -> { 
                    ctx.sessao.deslogar(); 
                    ok("Sessão encerrada."); 
                }
        ));

        // Comando para testar direcionamento automático (apenas para desenvolvimento)
        comandos.add(new ComandoFuncional(
                "[DEV] Testar Direcionamento", 
                u -> u instanceof Passageiro,
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

        // ✅ NOVO COMANDO: Testar Sistema de Avaliação
        comandos.add(new ComandoFuncional(
                "[DEV] Testar Sistema de Avaliação", 
                u -> u != null,
                (ctx, in) -> {
                    try {
                        System.out.println("🚀 INICIANDO TESTE DO SISTEMA DE AVALIAÇÃO");
                        System.out.println("===========================================");
                        
                        // Criar corrida de teste
                        var corrida = ctx.servicoCorrida.solicitarCorrida(
                            ctx.sessao.getUsuarioAtual().getEmail(),
                            "Av. Teste, 123",
                            "Rua Exemplo, 456", 
                            com.uberpb.model.CategoriaVeiculo.UBERX,
                            com.uberpb.model.MetodoPagamento.CARTAO
                        );
                        
                        // Simular motorista
                        corrida.setMotoristaAlocado("motorista_teste@email.com");
                        corrida.setStatus(com.uberpb.model.CorridaStatus.CONCLUIDA);
                        ctx.repositorioCorrida.atualizar(corrida);
                        
                        System.out.println("✅ Corrida de teste criada: " + corrida.getId().substring(0, 8));
                        
                        // Testar avaliação
                        if (ctx.sessao.getUsuarioAtual() instanceof com.uberpb.model.Passageiro) {
                            ctx.servicoAvaliacao.avaliarMotorista(
                                corrida.getId(),
                                ctx.sessao.getUsuarioAtual().getEmail(),
                                5,
                                "Excelente serviço! Motorista muito atencioso."
                            );
                            System.out.println("✅ Avaliação do motorista registrada com sucesso!");
                        }
                        
                        ok("Teste do sistema de avaliação concluído com sucesso!");
                        
                    } catch (Exception e) {
                        erro("Erro no teste: " + e.getMessage());
                    }
                }
        ));

        return comandos;
    }
}