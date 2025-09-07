package com.uberpb.exceptions;

public class EmailJaExistenteException extends RuntimeException {
    public EmailJaExistenteException(String message) {
        super(message);
    }
}