package com.rostrata.idle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Must be enabled so preflight + credentialed requests get CORS headers from CorsConfigurationSource.
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/trees", "/trees/seed-defaults").permitAll()
                        .requestMatchers("/api/auth/me", "/trees/*/chop").authenticated()
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
