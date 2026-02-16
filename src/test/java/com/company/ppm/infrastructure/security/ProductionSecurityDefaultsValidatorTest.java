package com.company.ppm.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductionSecurityDefaultsValidatorTest {

    @Test
    void allowsNonProdStartupWithLocalDefaults() {
        Environment environment = mock(Environment.class);
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("216d7273da054153237a869376000879");
        BootstrapAdminProperties bootstrapAdminProperties = new BootstrapAdminProperties();
        bootstrapAdminProperties.setEnabled(true);
        bootstrapAdminProperties.setEmail("admin@example.com");
        bootstrapAdminProperties.setPassword("AdminPass123!");
        CorsProperties corsProperties = new CorsProperties();
        corsProperties.setAllowedOrigins(List.of("http://localhost:3000"));

        ProductionSecurityDefaultsValidator validator = new ProductionSecurityDefaultsValidator(
                environment, jwtProperties, bootstrapAdminProperties, corsProperties
        );

        assertThatCode(() -> validator.run(mock(ApplicationArguments.class))).doesNotThrowAnyException();
    }

    @Test
    void failsProdStartupWithInsecureDefaults() {
        Environment environment = mock(Environment.class);
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("216d7273da054153237a869376000879");
        BootstrapAdminProperties bootstrapAdminProperties = new BootstrapAdminProperties();
        bootstrapAdminProperties.setEnabled(true);
        bootstrapAdminProperties.setEmail("admin@example.com");
        bootstrapAdminProperties.setPassword("AdminPass123!");
        CorsProperties corsProperties = new CorsProperties();
        corsProperties.setAllowedOrigins(List.of("http://localhost:5173"));

        ProductionSecurityDefaultsValidator validator = new ProductionSecurityDefaultsValidator(
                environment, jwtProperties, bootstrapAdminProperties, corsProperties
        );

        assertThatThrownBy(() -> validator.run(mock(ApplicationArguments.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PPM_JWT_SECRET")
                .hasMessageContaining("PPM_BOOTSTRAP_ADMIN_EMAIL")
                .hasMessageContaining("PPM_BOOTSTRAP_ADMIN_PASSWORD")
                .hasMessageContaining("PPM_ALLOWED_ORIGINS");
    }

    @Test
    void allowsProdStartupWithExplicitSecureValues() {
        Environment environment = mock(Environment.class);
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("c0ab6f919d0c4065acdd973f0102694f");
        BootstrapAdminProperties bootstrapAdminProperties = new BootstrapAdminProperties();
        bootstrapAdminProperties.setEnabled(true);
        bootstrapAdminProperties.setEmail("ops-admin@company.com");
        bootstrapAdminProperties.setPassword("S3cur3-Admin-Password");
        CorsProperties corsProperties = new CorsProperties();
        corsProperties.setAllowedOrigins(List.of("https://ppm.company.com"));

        ProductionSecurityDefaultsValidator validator = new ProductionSecurityDefaultsValidator(
                environment, jwtProperties, bootstrapAdminProperties, corsProperties
        );

        assertThatCode(() -> validator.run(mock(ApplicationArguments.class))).doesNotThrowAnyException();
    }
}
