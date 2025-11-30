package com.GreenThumb.api.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration("apigatewaySecurityConfig")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cette chaîne ne s'applique qu'aux URLs d'images : elle ne doit pas utiliser `anyRequest()`
        http
                .securityMatcher("/users/**", "/uploads/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // autorise l'accès public aux ressources statiques
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}