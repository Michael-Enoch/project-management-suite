package com.company.ppm.infrastructure.security;

import com.company.ppm.domain.model.RoleName;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.PasswordHasherPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {

    private final BootstrapAdminProperties bootstrapAdminProperties;
    private final UserAccountPort userAccountPort;
    private final PasswordHasherPort passwordHasherPort;

    public BootstrapAdminInitializer(
            BootstrapAdminProperties bootstrapAdminProperties,
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort
    ) {
        this.bootstrapAdminProperties = bootstrapAdminProperties;
        this.userAccountPort = userAccountPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapAdminProperties.isEnabled()) {
            return;
        }
        String email = bootstrapAdminProperties.getEmail();
        String password = bootstrapAdminProperties.getPassword();
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        String normalizedEmail = normalizeEmail(email);
        UserAccount existing = userAccountPort.findByEmail(normalizedEmail).orElse(null);
        if (existing == null) {
            createBootstrapAdmin(normalizedEmail, password);
            return;
        }

        if (isBootstrapAdminUpToDate(existing, password)) {
            return;
        }

        Set<RoleName> roles = new HashSet<>(existing.roles() == null ? Set.of() : existing.roles());
        roles.add(RoleName.ADMIN);

        userAccountPort.save(new UserAccount(
                existing.id(),
                normalizedEmail,
                passwordHasherPort.encode(password),
                true,
                Set.copyOf(roles),
                Set.of(),
                existing.createdAt()
        ));
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void createBootstrapAdmin(String normalizedEmail, String password) {
        userAccountPort.save(new UserAccount(
                UUID.randomUUID(),
                normalizedEmail,
                passwordHasherPort.encode(password),
                true,
                Set.of(RoleName.ADMIN),
                Set.of(),
                Instant.now()
        ));
    }

    private boolean isBootstrapAdminUpToDate(UserAccount existing, String rawPassword) {
        boolean hasAdminRole = existing.roles() != null && existing.roles().contains(RoleName.ADMIN);
        boolean passwordMatches = passwordHasherPort.matches(rawPassword, existing.passwordHash());
        return existing.active() && hasAdminRole && passwordMatches;
    }
}
