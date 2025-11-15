package com.burnoutinhos.burnoutinhos_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Central configuration for security-related beans that are shared across the application.
 *
 * Exposes a PasswordEncoder bean so other components (services, authentication handlers, tests)
 * can inject it without creating cycles or depending on service classes.
 */
@Configuration(proxyBeanMethods = false)
public class SecurityBeansConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
