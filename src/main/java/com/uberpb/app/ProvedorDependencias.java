package com.uberpb.app;

import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioOfertaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.*;
import com.uberpb.model.Administrador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    public static ContextoAplicacao fornecerContexto() {
        // Repositórios
        RepositorioUsuario repoUsuario = new ImplRepositorioUsuarioArquivo();
        RepositorioCorrida repoCorrida = new ImplRepositorioCorridaArquivo();
        RepositorioOferta  repoOferta  = new ImplRepositorioOfertaArquivo();

        // Seed admin
        semearAdminSePreciso(repoUsuario);

        // Serviços auxiliares
        // Atenção: ServicoCorrida usa EstimativaCorrida e ServicoLocalizacao (não confundir com EstimativaChegada)
        EstimativaCorrida  estimativaCorrida = new EstimativaCorrida(0, 0, 0);
        ServicoLocalizacao servLoc           = new ServicoLocalizacao();

        // Serviços de negócio existentes (ajuste se necessário)
        ServicoCadastro     servCadastro = new ServicoCadastro(repoUsuario); // sua versão que existe
        ServicoAutenticacao servAuth     = new ServicoAutenticacao(repoUsuario);
        ServicoCorrida      servCorrida  = new ServicoCorrida(repoCorrida, repoUsuario, estimativaCorrida, servLoc);
        ServicoOferta       servOferta   = new ServicoOferta(repoOferta, repoUsuario, repoCorrida);
        ServicoPagamento    servPagto    = new ServicoPagamento(repoCorrida, repoUsuario);
        GerenciadorCorridasAtivas ger    = new GerenciadorCorridasAtivas();
        Sessao sessao = new Sessao();

        // Serviços opcionais (ainda não usados ou ausentes na sua base): deixamos null
        ServicoValidacaoMotorista servValid = null;
        ServicoAvaliacao          servAval  = null;
        ServicoOtimizacaoRota     servOpt   = null;
        ServicoDirecionamentoCorrida servDirec = null;
        EstimativaChegada         servEta   = null;
        ServicoAdmin              servAdmin = null;

        // Constrói o contexto com o CONSTRUTOR COMPLETO
        return new ContextoAplicacao(
                sessao,
                repoUsuario,
                servCadastro,
                servAuth,
                repoCorrida,
                servCorrida,
                repoOferta,
                servOferta,
                servValid,     // ServicoValidacaoMotorista (null por ora)
                servPagto,
                servAval,      // ServicoAvaliacao (null)
                servOpt,       // ServicoOtimizacaoRota (null)
                servLoc,
                servDirec,     // ServicoDirecionamentoCorrida (null)
                servEta,       // EstimativaChegada (null)
                servAdmin,     // ServicoAdmin (null)
                ger
        );
    }

    /** Compatível com AplicacaoCLI que chama sem args; monta lista baseada no seu conjunto atual de comandos. */
    public static List<Comando> fornecerComandosPadrao() {
        ContextoAplicacao ctx = fornecerContexto();
        return fornecerComandosPadrao(ctx);
    }

    /** Ajuste a lista aos comandos que você REALMENTE tem. */
    public static List<Comando> fornecerComandosPadrao(ContextoAplicacao ctx) {
        List<Comando> lista = new ArrayList<>();
        // Telas iniciais
        lista.add(new LoginComando());
        lista.add(new CadastrarPassageiroComando());
        lista.add(new CadastrarMotoristaComando());

        // Passageiro
        lista.add(new SolicitarCorridaComando());
        lista.add(new AcompanharCorridaComando());
        lista.add(new ConcluirCorridaComando());
        lista.add(new VisualizarCorridaComando());
        // Se existirem no seu projeto, descomente:
        // lista.add(new VerPagamentoComando());
        // lista.add(new AvaliarCorridaComando());
        // lista.add(new VisualizarMinhasAvaliacoesComando());
        // lista.add(new HistoricoFiltradoComando());

        // Motorista
        lista.add(new MotoristaVerOfertasComando());

        // Admin — se tiver comandos específicos, adicione-os e use visivelPara() para restringir
        // lista.add(new AdminPainelComando());

        return lista;
    }

    // ============ Seed Admin ============
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
