package com.uberpb.exceptions;

public class CredenciaisInvalidasException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CredenciaisInvalidasException(String message) {
        super(message);
    }
}