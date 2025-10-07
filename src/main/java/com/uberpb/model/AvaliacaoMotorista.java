package com.uberpb.model;

public class AvaliacaoMotorista extends Avaliacao {
    private final String passageiroEmail; // Avaliado
    private final String motoristaEmail; // Avaliador
    
    public AvaliacaoMotorista(String corridaId, String passageiroEmail, 
                             String motoristaEmail, int rating, String comentario) {
        super(corridaId, rating, comentario);
        this.passageiroEmail = passageiroEmail;
        this.motoristaEmail = motoristaEmail;
    }
    
    // Getters
    public String getPassageiroEmail() { return passageiroEmail; }
    public String getMotoristaEmail() { return motoristaEmail; }
    
    @Override
    public String toStringParaPersistencia() {
        return String.join("|", 
            "MOTORISTA_TO_PASSAGEIRO",
            id,
            corridaId,
            passageiroEmail,
            motoristaEmail,
            String.valueOf(rating),
            comentario,
            dataAvaliacao.toString()
        );
    }
    
    public static AvaliacaoMotorista fromStringParaPersistencia(String linha) {
        String[] partes = linha.split("\\|", -1);
        return new AvaliacaoMotorista(
            partes[2], // corridaId
            partes[3], // passageiroEmail
            partes[4], // motoristaEmail
            Integer.parseInt(partes[5]), // rating
            partes[6]  // comentario
        );
    }
}