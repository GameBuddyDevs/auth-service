package com.back2261.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors()
                .disable()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/register", "/auth/verify", "/auth/username")
                .permitAll()
                .and()
                .build();
    }
}
