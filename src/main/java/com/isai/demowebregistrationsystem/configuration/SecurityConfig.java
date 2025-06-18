package com.isai.demowebregistrationsystem.configuration;

import com.isai.demowebregistrationsystem.services.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF, considera habilitarlo con tokens para producción
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a todas las rutas de autenticación y registro bajo /auth/
                        // ¡VERIFICA DOBLEMENTE ESTAS RUTAS!
                        .requestMatchers(
                                "/",
                                "/auth/login", // Tu página de login real
                                "/auth/registro", // La página de selección de rol
                                "/auth/registro/form/**", // Los formularios de registro específicos (ej. /auth/registro/form/docente)
                                "/auth/registro/guardar/**", // Los endpoints para guardar los registros
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll() // ¡ESTO ES CLAVE! Asegura que estas rutas NO sean protegidas.

                        // Reglas de acceso basado en roles para los dashboards
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/docente/**").hasAnyRole("ADMIN", "DOCENTE")
                        .requestMatchers("/estudiante/**").hasAnyRole("ADMIN", "ESTUDIANTE")
                        .requestMatchers("/apoderado/**").hasAnyRole("ADMIN", "APODERADO")

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // Página que muestra el formulario
                        .loginProcessingUrl("/auth/do-login") // URL donde se procesa el login
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para cerrar sesión (normalmente un POST)
                        .logoutSuccessUrl("/auth/login?logout=true") // A dónde ir después de cerrar sesión
                        .invalidateHttpSession(true) // Invalida la sesión HTTP
                        .deleteCookies("JSESSIONID") // Borra la cookie de sesión
                        .permitAll() // Asegura que la URL de logout sea accesible
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied") // Página para acceso denegado
                );

        return http.build();
    }
}