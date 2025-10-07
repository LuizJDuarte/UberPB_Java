package com.uberpb.model;

public class AvaliacaoPassageiro extends Avaliacao {
    private final String motoristaEmail; // Avaliado
    private final String passageiroEmail; // Avaliador
    
    public AvaliacaoPassageiro(String corridaId, String motoristaEmail, 
                              String passageiroEmail, int rating, String comentario) {
        super(corridaId, rating, comentario);
        this.motoristaEmail = motoristaEmail;
        this.passageiroEmail = passageiroEmail;
    }
    
    // Getters
    public String getMotoristaEmail() { return motoristaEmail; }
    public String getPassageiroEmail() { return passageiroEmail; }
    
    @Override
    public String toStringParaPersistencia() {
        return String.join("|", 
            "PASSAGEIRO_TO_MOTORISTA",
            id,
            corridaId,
            motoristaEmail,
            passageiroEmail,
            String.valueOf(rating),
            comentario,
            dataAvaliacao.toString()
        );
    }
    
    public static AvaliacaoPassageiro fromStringParaPersistencia(String linha) {
        String[] partes = linha.split("\\|", -1);
        return new AvaliacaoPassageiro(
            partes[2], // corridaId
            partes[3], // motoristaEmail
            partes[4], // passageiroEmail
            Integer.parseInt(partes[5]), // rating
            partes[6]  // comentario
        );
    }
}