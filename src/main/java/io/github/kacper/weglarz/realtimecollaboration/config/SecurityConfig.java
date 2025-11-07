package io.github.kacper.weglarz.realtimecollaboration.config;

import io.github.kacper.weglarz.realtimecollaboration.security.jwt.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    /**
     * Configures application security
     * Allows access to /register and /login without authentication
     * @param http HTTP security configuration
     * @return the configured security filter chain
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jWTFilter) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/loginsignup.html","/loginsignup.js", "userprofile.html", "userprofile.js",
                                        "/api/auth/register","/api/auth/login", "/style.css").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jWTFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .build();
    }


    /**
     * Creates Registers a password encoder bean using the {@link BCryptPasswordEncoder} algorithm
     * @return a password encoder for hashing and verifying user passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
