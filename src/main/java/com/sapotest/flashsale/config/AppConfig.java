package com.sapotest.flashsale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * General application configurations.
 */
@Configuration
public class AppConfig {

    /**
     * Define the password encoder bean using BCrypt hashing algorithm.
     * Used for securely hashing and matching user passwords.
     * * @return BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}