package com.sapotest.flashsale.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration to handle Cross-Origin Resource Sharing (CORS).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mapping to allow the React frontend to communicate with the API.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Allow requests from the React/Vite development server
                .allowedOrigins("http://localhost:5173")
                // Allow standard HTTP methods for RESTful operations
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Allow all headers for flexibility (JWT, Content-Type, etc.)
                .allowedHeaders("*")
                // Allow cookies and credentials to be sent across origins
                .allowCredentials(true);
    }
}