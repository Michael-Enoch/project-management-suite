package com.company.ppm.infrastructure.security;

import com.company.ppm.domain.model.RoleName;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.PasswordHasherPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapAdminInitializerTest {

    @Mock
    private UserAccountPort userAccountPort;
    @Mock
    private PasswordHasherPort passwordHasherPort;

    private BootstrapAdminProperties properties;
    private BootstrapAdminInitializer initializer;

    @BeforeEach
    void setUp() {
        properties = new BootstrapAdminProperties();
        properties.setEnabled(true);
        properties.setEmail("  ADMIN@EXAMPLE.COM  ");
        properties.setPassword("AdminPass123!");
        initializer = new BootstrapAdminInitializer(properties, userAccountPort, passwordHasherPort);
    }

    @Test
    void createsBootstrapAdminWhenMissing() {
        when(userAccountPort.findByEmail("admin@example.com")).thenReturn(java.util.Optional.empty());
        when(passwordHasherPort.encode("AdminPass123!")).thenReturn("encoded");

        initializer.run(mock(ApplicationArguments.class));

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountPort).save(captor.capture());
        UserAccount saved = captor.getValue();
        assertThat(saved.email()).isEqualTo("admin@example.com");
        assertThat(saved.passwordHash()).isEqualTo("encoded");
        assertThat(saved.active()).isTrue();
        assertThat(saved.roles()).contains(RoleName.ADMIN);
    }

    @Test
    void updatesExistingAccountWhenPasswordOrRoleIsOutdated() {
        UUID userId = UUID.randomUUID();
        UserAccount existing = new UserAccount(
                userId,
                "admin@example.com",
                "old-hash",
                false,
                Set.of(RoleName.VIEWER),
                Set.of(),
                Instant.now().minusSeconds(3600)
        );
        when(userAccountPort.findByEmail("admin@example.com")).thenReturn(java.util.Optional.of(existing));
        when(passwordHasherPort.matches("AdminPass123!", "old-hash")).thenReturn(false);
        when(passwordHasherPort.encode("AdminPass123!")).thenReturn("new-hash");

        initializer.run(mock(ApplicationArguments.class));

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountPort).save(captor.capture());
        UserAccount updated = captor.getValue();
        assertThat(updated.id()).isEqualTo(userId);
        assertThat(updated.active()).isTrue();
        assertThat(updated.roles()).contains(RoleName.ADMIN, RoleName.VIEWER);
        assertThat(updated.passwordHash()).isEqualTo("new-hash");
    }

    @Test
    void doesNothingWhenExistingAdminIsAlreadyUpToDate() {
        UserAccount existing = new UserAccount(
                UUID.randomUUID(),
                "admin@example.com",
                "current-hash",
                true,
                Set.of(RoleName.ADMIN),
                Set.of(),
                Instant.now().minusSeconds(3600)
        );
        when(userAccountPort.findByEmail("admin@example.com")).thenReturn(java.util.Optional.of(existing));
        when(passwordHasherPort.matches("AdminPass123!", "current-hash")).thenReturn(true);

        initializer.run(mock(ApplicationArguments.class));

        verify(userAccountPort, never()).save(any());
        verify(passwordHasherPort, never()).encode(any());
    }
}
