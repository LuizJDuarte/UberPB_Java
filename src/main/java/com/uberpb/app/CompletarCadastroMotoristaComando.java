package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;

import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

/**
 * CLI: motorista informa CNH/CRLV e dados do veículo (RF02).
 * O serviço calculará categorias e ativará a conta se tudo ok.
 */
public class CompletarCadastroMotoristaComando implements Comando {

    @Override public String nome() { return "Completar cadastro (docs + veículo)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Motorista;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner in) {
        String email = contexto.sessao.getUsuarioAtual().getEmail();

        System.out.println("== Documentos ==");
        boolean cnh = lerBoolean(in, "CNH válida? (s/n): ");
        boolean crlv = lerBoolean(in, "CRLV válido? (s/n): ");

        System.out.println("== Veículo ==");
        System.out.print("Modelo: ");       String modelo = in.nextLine().trim();
        int ano = lerInt(in, "Ano (ex.: 2021): ");
        System.out.print("Placa: ");        String placa = in.nextLine().trim();
        System.out.print("Cor: ");          String cor = in.nextLine().trim();
        int capacidade = lerInt(in, "Capacidade de passageiros (ex.: 5): ");
        System.out.print("Tamanho porta-malas (P/M/G): "); String malas = in.nextLine().trim();

        Veiculo v = new Veiculo(modelo, ano, placa, cor, capacidade, malas);

        Motorista m = contexto.servicoValidacaoMotorista
                .registrarDocumentosEVeiculo(email, cnh, crlv, v);

        ok("Cadastro atualizado!");
        System.out.println("- CNH válida: " + m.isCnhValida());
        System.out.println("- CRLV válido: " + m.isCrlvValido());
        System.out.println("- Categorias: " + (m.getVeiculo() != null ? m.getVeiculo().getCategoriasDisponiveis() : "n/d"));
        System.out.println("- Conta ativa: " + (m.isContaAtiva() ? "SIM" : "NÃO"));
        System.out.println("(Dados persistidos em data/usuarios.txt)");
    }

    private boolean lerBoolean(Scanner in, String msg) {
        System.out.print(msg);
        String s = in.nextLine().trim();
        return s.equalsIgnoreCase("s") || s.equalsIgnoreCase("sim") || s.equalsIgnoreCase("y");
    }

    private int lerInt(Scanner in, String msg) {
        while (true) {
            System.out.print(msg);
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (Exception e) { erro("Valor inválido. Tente novamente."); }
        }
    }
}
