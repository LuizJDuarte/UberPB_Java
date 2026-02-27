package com.uberpb.app;


import com.uberpb.model.Usuario;
import com.uberpb.model.Localizacao;
import com.uberpb.model.Restaurante;


import java.util.Scanner;


public class CadastrarRestauranteComando implements Comando {


    @Override
    public String nome() {
        return "Cadastrar Restaurante";
    }


    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull == null;
    }


    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.print("Email: ");
        String email = entrada.nextLine();
        System.out.print("Senha: ");
        String senha = entrada.nextLine();
        System.out.print("Nome fantasia: ");
        String nome = entrada.nextLine();
        System.out.print("Endereço do Restaurante (ex: Rua Floriano Peixoto): ");
        String endereco = entrada.nextLine();
        System.out.print("Taxa de entrega (ex: 5.00): ");
        double taxa = Double.parseDouble(entrada.nextLine().replace(",", "."));


        System.out.print("Tempo estimado (minutos): ");
        int tempo = Integer.parseInt(entrada.nextLine());


        Restaurante r = contexto.servicoCadastro.cadastrarRestaurante(email, senha, nome, "");
        Localizacao loc = contexto.servicoLocalizacao.geocodificar(endereco);
        r.setLocalizacao(loc);
        r.setTaxaEntrega(taxa);
        r.setTempoEstimadoEntregaMinutos(tempo);
        r.setContaAtiva(true);


        contexto.repositorioRestaurante.salvar(r);
       


        System.out.println("OK! Restaurante cadastrado em: " + loc.latitude() + ", " + loc.longitude());
    }
}
