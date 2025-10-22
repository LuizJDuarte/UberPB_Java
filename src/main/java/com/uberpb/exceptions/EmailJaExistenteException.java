package com.uberpb.exceptions;

public class EmailJaExistenteException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -738307999184919977L;

	public EmailJaExistenteException(String message) {
        super(message);
    }
}