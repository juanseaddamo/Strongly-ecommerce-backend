package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.uade.tpo.demo.entity.enums.Role;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                 // Endpoints públicos                                 
                                 .requestMatchers("/api/v1/auth/**").permitAll() // auth
                                .requestMatchers("/error/**").permitAll() // manejo de errores
                                .requestMatchers(HttpMethod.GET,"/product/**").permitAll() // listar productos 
                                .requestMatchers(HttpMethod.GET,"/categories/**").permitAll() // categorías 
                                .requestMatchers(HttpMethod.GET,"/offers/**").permitAll() // ofertas 
                                .requestMatchers(HttpMethod.GET,"/brands/**").permitAll() // marcas 
                                .requestMatchers(HttpMethod.GET,"/support/**").permitAll() // soporte 
                                // Endpoints solo para compradores                                
                                .requestMatchers("/cart/**").hasAnyAuthority(Role.BUYER.name()) // carrito
                                .requestMatchers("/orders/**").hasAuthority(Role.BUYER.name()) // órdenes
                                .requestMatchers("/users/profile/**").hasAuthority(Role.BUYER.name()) // perfil
                                 // Endpoints solo para vendedores/admin
                                .requestMatchers(HttpMethod.POST,"/product/**").hasAnyAuthority(Role.SELLER.name(), Role.ADMIN.name()) // crear productos
                                .requestMatchers(HttpMethod.PUT,"/product/**").hasAnyAuthority(Role.SELLER.name(), Role.ADMIN.name()) // actualizar productos                                
                                .requestMatchers("/inventory/**").hasAnyAuthority(Role.SELLER.name(), Role.ADMIN.name()) // inventario
                                .requestMatchers(HttpMethod.POST,"/offers/create/**").hasAnyAuthority(Role.SELLER.name(), Role.ADMIN.name()) // crear ofertas
                                .requestMatchers(HttpMethod.PUT,"/offers/create/**").hasAnyAuthority(Role.SELLER.name(), Role.ADMIN.name()) // crear ofertas
                                // Endpoints solo para admin
                                .requestMatchers(HttpMethod.DELETE,"/product/**").hasAuthority(Role.ADMIN.name()) // eliminar productos
                                .requestMatchers("/users/**").hasAnyAuthority(Role.ADMIN.name(),Role.SELLER.name(), Role.BUYER.name()) // gestión de usuarios
                                .requestMatchers(HttpMethod.DELETE,"/offers/**").hasAuthority(Role.ADMIN.name()) // eliminar ofertas
                                .anyRequest()
                                .authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}