package com.elolympus.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

    private final PasswordEncoder passwordEncoder;
    public PasswordUtils(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Método para encriptar una contraseña
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
