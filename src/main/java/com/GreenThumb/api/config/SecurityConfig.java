package com.GreenThumb.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/count",
                                "/api/messages/top3like",
                                "/api/resources/three-resources",
                                "/api/sessions",
                                "/api/resources/three-resources",
                                "/api/test/mail")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
