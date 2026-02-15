package com.company.ppm.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security.cors")
public class CorsProperties {

    private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:3000", "http://localhost:5173"));

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
