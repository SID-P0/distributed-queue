package com.backend.distributedqueue.configuration; // Or your preferred configuration package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] SWAGGER_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",      // OpenAPI v3 specification for springdoc-openapi
            "/api-docs/**",          // General API docs if you've customized the path
            "/swagger-ui/**",        // Swagger UI resources (CSS, JS, etc.)
            // Note: "/swagger-ui.html" is already covered by /swagger-ui/** but can be kept for clarity
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(SWAGGER_WHITELIST).permitAll() // Allow unauthenticated access to Swagger
                                .anyRequest().authenticated()                // All other requests require authentication
                )
                .httpBasic(withDefaults()) // Enable basic authentication for other endpoints (or your preferred auth method)
                .csrf(csrf -> csrf.disable()); // Often disabled for stateless APIs; consider your security needs.
        // If your API is stateful or uses cookies for auth,
        // you'll need a proper CSRF configuration.
        return http.build();
    }

}