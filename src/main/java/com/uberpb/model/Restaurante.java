package com.uberpb.model;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;


public class Restaurante extends Usuario {


    private String nomeFantasia;
    private String cnpj;
    private boolean contaAtiva;
    private List<ItemCardapio> cardapio;
    private double taxaEntrega;
    private int tempoEstimadoEntregaMinutos;
    private Localizacao localizacao;


    public Restaurante(String email, String senhaHash) {
        super(email, senhaHash);
        this.nomeFantasia = "";
        this.cnpj = "";
        this.contaAtiva = true; // Por padrão ativo
        this.localizacao = new Localizacao(0, 0);
        this.cardapio = new ArrayList<>();
        this.taxaEntrega = 0.0;
        this.tempoEstimadoEntregaMinutos = 30;
    }


    // Getters e Setters
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public Localizacao getLocalizacao() { return localizacao; }
    public void setLocalizacao(Localizacao localizacao) { this.localizacao = localizacao; }
    public boolean isContaAtiva() { return contaAtiva; }
    public void setContaAtiva(boolean contaAtiva) { this.contaAtiva = contaAtiva; }
    public List<ItemCardapio> getCardapio() { return cardapio; }
    public void setCardapio(List<ItemCardapio> cardapio) { this.cardapio = cardapio; }
    public void adicionarItemCardapio(ItemCardapio item) { this.cardapio.add(item); }
    public double getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(double taxaEntrega) { this.taxaEntrega = taxaEntrega; }
    public int getTempoEstimadoEntregaMinutos() { return tempoEstimadoEntregaMinutos; }
    public void setTempoEstimadoEntregaMinutos(int tempoEstimadoEntregaMinutos) { this.tempoEstimadoEntregaMinutos = tempoEstimadoEntregaMinutos; }


    /**
     *  CORREÇÃO HIERÁRQUICA:
     * 1. Nível 1 (Campos do Restaurante): Separados por VÍRGULA (,)
     * 2. Nível 2 (Lista de Itens): Separados por PIPE (|)
     * 3. Nível 3 (Campos do Item): Separados por HASHTAG (#) -> definido em ItemCardapio
     */
    @Override
    public String toStringParaPersistencia() {
        String cardapioStr = cardapio.stream()
                                     .map(ItemCardapio::toStringParaPersistencia)
                                     .collect(Collectors.joining("|"));


        // Limpamos vírgulas dos nomes para não quebrar o split principal
        String nomeLimpo = nomeFantasia.replace(",", " ");
        String cnpjLimpo = cnpj.replace(",", " ");


        return String.format(Locale.US, "RESTAURANTE,%s,%s,%s,%s,%s,%.6f,%.6f,%.2f,%d,%s",
            getEmail(),
            getSenhaHash(),
            nomeLimpo,
            cnpjLimpo,
            contaAtiva,
            localizacao.latitude(),
            localizacao.longitude(),
            taxaEntrega,
            tempoEstimadoEntregaMinutos,
            cardapioStr.isEmpty() ? "VAZIO" : cardapioStr
        );
    }


    public static Restaurante fromString(String linha) {
        if (linha == null || !linha.startsWith("RESTAURANTE")) return null;
       
        // O split(",") só vai atuar nos campos do Restaurante
        String[] partes = linha.split(",", -1);
        if (partes.length < 11) return null;


        try {
            Restaurante r = new Restaurante(partes[1], partes[2]);
            r.setNomeFantasia(partes[3]);
            r.setCnpj(partes[4]);
            r.setContaAtiva(Boolean.parseBoolean(partes[5]));
            r.setLocalizacao(new Localizacao(Double.parseDouble(partes[6]), Double.parseDouble(partes[7])));
            r.setTaxaEntrega(Double.parseDouble(partes[8]));
            r.setTempoEstimadoEntregaMinutos(Integer.parseInt(partes[9]));


            String cardapioBruto = partes[10];
            if (!"VAZIO".equals(cardapioBruto) && !cardapioBruto.isEmpty()) {
                // Divide a string gigante pelos PIPES (|) para pegar cada item
                String[] itensStr = cardapioBruto.split("\\|");
                for (String s : itensStr) {
                    ItemCardapio item = ItemCardapio.fromStringParaPersistencia(s);
                    if (item != null) r.adicionarItemCardapio(item);
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public String toString() {
        return String.format("%s (CNPJ: %s) [Taxa: R$ %.2f, %s]", nomeFantasia, cnpj,taxaEntrega, (contaAtiva ? "Ativa" : "Inativa"));
    }
}


