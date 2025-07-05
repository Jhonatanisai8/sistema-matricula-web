package com.isai.demowebregistrationsystem;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

public class GenerarClave {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String clave = passwordEncoder.encode("claves123");
        System.out.println(clave);

        String codigo = "DOC00";
        Random random = new Random();
        Integer numeroGenerado = random.nextInt(1000);
        System.out.println(numeroGenerado);

    }
}
