package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.repository.*;
import com.uberpb.service.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    public static ContextoAplicacao fornecerContexto() {
        // Repositórios
        RepositorioUsuario repoUsuario = ImplRepositorioUsuarioArquivo.getInstance();
        RepositorioCorrida repoCorrida = ImplRepositorioCorridaArquivo.getInstance();
        RepositorioOferta repoOferta = ImplRepositorioOfertaArquivo.getInstance();
        RepositorioAvaliacao repoAvaliacao = ImplRepositorioAvaliacaoArquivo.getInstance();

        // Seed admin
        semearAdminSePreciso(repoUsuario);

        // Serviços auxiliares
        EstimativaCorrida estimativaCorrida = new EstimativaCorrida(0, 0, 0);
        ServicoLocalizacao servLoc = new ServicoLocalizacao();

        // Serviços de negócio
        ServicoCadastro servCadastro = new ServicoCadastro(repoUsuario);
        ServicoAutenticacao servAuth = new ServicoAutenticacao(repoUsuario);
        ServicoCorrida servCorrida = new ServicoCorrida(repoCorrida, repoUsuario, estimativaCorrida, servLoc);
        ServicoOferta servOferta = new ServicoOferta(repoOferta, repoUsuario, repoCorrida);
        ServicoPagamento servPagto = new ServicoPagamento(repoCorrida, repoUsuario);
        ServicoAvaliacao servAval = new ServicoAvaliacao(repoAvaliacao, repoCorrida, repoUsuario);
        GerenciadorCorridasAtivas ger = new GerenciadorCorridasAtivas();
        Sessao sessao = new Sessao();

        // Serviços opcionais (ainda não usados ou ausentes na sua base): deixamos null
        ServicoValidacaoMotorista servValid = null;
        ServicoOtimizacaoRota servOpt = null;
        ServicoDirecionamentoCorrida servDirec = null;
        EstimativaChegada servEta = null;
        ServicoAdmin servAdmin = null;

        // Constrói o contexto com o CONSTRUTOR COMPLETO
        return new ContextoAplicacao(
            sessao,
            repoUsuario,
            servCadastro,
            servAuth,
            repoCorrida,
            servCorrida,
            repoOferta,
            repoAvaliacao, 
            servOferta,
            servValid,
            servPagto,
            servAval,
            servOpt,
            servLoc,
            servDirec,
            servEta,
            servAdmin,
            ger);
    }

    public static List<Comando> fornecerComandos() {
        // Retorna todos os comandos possíveis
        List<Comando> lista = new ArrayList<>();

        // Comandos de Sessão
        lista.add(new LoginComando());
        lista.add(new LogoutComando());

        // Cadastro
        lista.add(new CadastrarPassageiroComando());
        lista.add(new CadastrarMotoristaComando());
        lista.add(new CompletarCadastroMotoristaComando());
        lista.add(new VerificarStatusAprovacaoComando()); // Para motoristas pendentes

        // Passageiro
        lista.add(new SolicitarCorridaComando());
        lista.add(new CancelarCorridaComando());
        lista.add(new AvaliarMotoristaComando());

        // Motorista
        lista.add(new FicarOnlineOfflineComando());
        lista.add(new MotoristaVerOfertasComando());
        lista.add(new AtualizarLocalizacaoComando());
        // Adicione aceitar/recusar, iniciar, concluir...

        // Geral
        lista.add(new VisualizarHistoricoComando());

        // Admin
        lista.add(new MenuAdminComando());

        return lista;
    }

    private static void semearAdminSePreciso(RepositorioUsuario ru) {
        var existente = ru.buscarPorEmail("admin@uberpb.com");
        if (existente != null) return;
        String senhaHash = sha256Hex("admin123");
        var admin = new Administrador("admin@uberpb.com", senhaHash);
        ru.salvar(admin);
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao calcular SHA-256", e);
        }
    }
}
