package com.burnoutinhos.burnoutinhos_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JWTAuthFilter jwtAuthFilter;

    public SecurityConfiguration(JWTAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(banco ->
                banco.frameOptions(
                    HeadersConfigurer.FrameOptionsConfig::disable
                )
            )
            .authorizeHttpRequests(request ->
                request
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    )
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .sessionManagement(server ->
                server.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
