package com.mediahub.iam.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.mediahub.iam.repository.UserRepository;

// Central Spring Security configuration class for the application.
// @Configuration marks it as a bean source; @EnableWebSecurity turns on Spring Security's web support.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Dependencies needed to build the JWT filter: the user repository (to load users)
    // and JwtUtil (to validate tokens), both injected by Spring.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Registers the custom JwtFilter as a Spring bean, wiring in its two dependencies.
    // This is the filter that authenticates each request based on its JWT.
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, userRepository);
    }

    // Defines the security filter chain that governs how HTTP requests are secured.
    // Configures CSRF, session policy, URL authorization rules, and inserts the JWT filter.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection since this is a stateless token-based API (no session cookies to protect).
            .csrf(csrf -> csrf.disable())
            // Use stateless sessions — the server keeps no session state and relies entirely on the JWT per request.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Authorization rules: allow the auth endpoints (login/register/etc.) without a token,
            // and require authentication for every other request.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/mediaHub/iam/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            // Run our JwtFilter before Spring's username/password filter so the JWT is processed first.
            .addFilterBefore(jwtFilter(),
                UsernamePasswordAuthenticationFilter.class);

        // Build and return the configured SecurityFilterChain.
        return http.build();
    }
}