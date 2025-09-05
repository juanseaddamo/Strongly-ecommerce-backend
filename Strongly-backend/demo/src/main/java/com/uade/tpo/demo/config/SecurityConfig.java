package com.uade.tpo.demo.config;

import org.springframework.security.config.Customizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // para poder hacer POST desde Postman sin CSRF token
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**", "/actuator/**").permitAll() // público por ahora
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()); // o quitalo si no querés basic
        return http.build();
    }

}
