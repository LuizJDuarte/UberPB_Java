package com.uberpb.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordUtilTest {

    @Test
    public void testHashPassword() {
        String password = "admin";
        String expectedHash = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        String hashedPassword = PasswordUtil.hashPassword(password);
        assertEquals(expectedHash, hashedPassword);
    }
     @Test
    public void testValidEmail() {
        assertTrue(PasswordUtil.isValidEmail("test@example.com"));
        assertTrue(PasswordUtil.isValidEmail("test.name@example.co.uk"));
        assertTrue(PasswordUtil.isValidEmail("test_name@example.com"));

    }

    @Test
    public void testInvalidEmail() {
        assertFalse(PasswordUtil.isValidEmail("test@.com"));
        assertFalse(PasswordUtil.isValidEmail("@example.com"));
        assertFalse(PasswordUtil.isValidEmail("test@com"));
        assertFalse(PasswordUtil.isValidEmail("test@.com"));
        assertFalse(PasswordUtil.isValidEmail("test@com."));
        assertFalse(PasswordUtil.isValidEmail("test com"));
        assertFalse(PasswordUtil.isValidEmail("test"));
        assertFalse(PasswordUtil.isValidEmail(null));
    }
}
