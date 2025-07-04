package com.isai.demowebregistrationsystem;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GenerarClave {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String clave = passwordEncoder.encode("claves123");
        System.out.println(clave);
    }
}
