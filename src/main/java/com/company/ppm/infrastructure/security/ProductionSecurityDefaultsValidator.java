package com.company.ppm.infrastructure.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ProductionSecurityDefaultsValidator implements ApplicationRunner {

    private static final String DEFAULT_JWT_SECRET = "216d7273da054153237a869376000879";
    private static final String DEFAULT_BOOTSTRAP_EMAIL = "admin@example.com";
    private static final String DEFAULT_BOOTSTRAP_PASSWORD = "AdminPass123!";

    private final Environment environment;
    private final JwtProperties jwtProperties;
    private final BootstrapAdminProperties bootstrapAdminProperties;
    private final CorsProperties corsProperties;

    public ProductionSecurityDefaultsValidator(
            Environment environment,
            JwtProperties jwtProperties,
            BootstrapAdminProperties bootstrapAdminProperties,
            CorsProperties corsProperties
    ) {
        this.environment = environment;
        this.jwtProperties = jwtProperties;
        this.bootstrapAdminProperties = bootstrapAdminProperties;
        this.corsProperties = corsProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!environment.acceptsProfiles(Profiles.of("prod", "production"))) {
            return;
        }

        List<String> issues = new ArrayList<>();
        validateJwtSecret(issues);
        validateBootstrapAdmin(issues);
        validateCors(issues);

        if (!issues.isEmpty()) {
            throw new IllegalStateException("Invalid production security configuration: " + String.join("; ", issues));
        }
    }

    private void validateJwtSecret(List<String> issues) {
        String jwtSecret = jwtProperties.getSecret();
        if (jwtSecret == null || jwtSecret.isBlank() || DEFAULT_JWT_SECRET.equals(jwtSecret)) {
            issues.add("set a non-default PPM_JWT_SECRET");
        }
    }

    private void validateBootstrapAdmin(List<String> issues) {
        if (!bootstrapAdminProperties.isEnabled()) {
            return;
        }
        String email = bootstrapAdminProperties.getEmail();
        if (email == null || email.isBlank() || DEFAULT_BOOTSTRAP_EMAIL.equalsIgnoreCase(email.trim())) {
            issues.add("set a non-default PPM_BOOTSTRAP_ADMIN_EMAIL when bootstrap admin is enabled");
        }
        String password = bootstrapAdminProperties.getPassword();
        if (password == null || password.isBlank() || DEFAULT_BOOTSTRAP_PASSWORD.equals(password)) {
            issues.add("set a non-default PPM_BOOTSTRAP_ADMIN_PASSWORD when bootstrap admin is enabled");
        }
    }

    private void validateCors(List<String> issues) {
        List<String> allowedOrigins = corsProperties.getAllowedOrigins() == null
                ? List.of()
                : corsProperties.getAllowedOrigins().stream()
                .map(origin -> origin == null ? "" : origin.trim())
                .filter(origin -> !origin.isBlank())
                .toList();

        if (allowedOrigins.isEmpty()) {
            issues.add("set PPM_ALLOWED_ORIGINS with at least one frontend origin");
            return;
        }
        if (allowedOrigins.stream().anyMatch("*"::equals)) {
            issues.add("PPM_ALLOWED_ORIGINS cannot contain '*' when credentials are enabled");
        }
        if (allowedOrigins.stream().anyMatch(this::isLocalhostOrigin)) {
            issues.add("PPM_ALLOWED_ORIGINS cannot include localhost origins in production");
        }
    }

    private boolean isLocalhostOrigin(String origin) {
        String normalized = origin.toLowerCase(Locale.ROOT);
        return normalized.contains("localhost") || normalized.contains("127.0.0.1");
    }
}
