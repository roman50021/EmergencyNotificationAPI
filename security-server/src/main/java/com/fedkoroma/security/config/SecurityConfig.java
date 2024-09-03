package com.fedkoroma.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/register",
                                "/auth/token",
                                "/auth/validate",
                                "/auth/confirm",
                                "/swagger-ui.html",
                                "/swagger-ui/**", // Важно добавить этот путь
                                "/v3/api-docs/**" // Путь для API документации
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}

