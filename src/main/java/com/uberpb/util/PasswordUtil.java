package com.uberpb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtil {

    // Regex simples para validação de email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Gera o hash SHA-256 de uma senha.
     * @param password A senha em texto claro.
     * @return O hash SHA-256 da senha em formato hexadecimal.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            // Em um ambiente de produção, logar a exceção.
            // Para este projeto, um RuntimeException é aceitável, pois SHA-256 é esperado.
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Converte um array de bytes para uma string hexadecimal.
     * @param hash O array de bytes do hash.
     * @return A string hexadecimal.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Valida o formato de um e-mail usando uma expressão regular.
     * @param email O e-mail a ser validado.
     * @return true se o formato do e-mail for válido, false caso contrário.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    
    
}