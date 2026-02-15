package com.company.ppm.application.service;

import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.TokenResponse;
import com.company.ppm.domain.model.PermissionName;
import com.company.ppm.domain.model.RoleName;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.PasswordHasherPort;
import com.company.ppm.domain.port.out.RefreshTokenPort;
import com.company.ppm.domain.port.out.TokenPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UserAccountPort userAccountPort;
    @Mock
    private PasswordHasherPort passwordHasherPort;
    @Mock
    private TokenPort tokenPort;
    @Mock
    private RefreshTokenPort refreshTokenPort;
    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private AuthApplicationService authApplicationService;

    @Test
    void loginNormalizesEmailBeforeLookup() {
        UserAccount user = new UserAccount(
                UUID.randomUUID(),
                "user@example.com",
                "hashed",
                true,
                Set.of(RoleName.ADMIN),
                Set.of(PermissionName.PROJECT_READ),
                Instant.now()
        );
        TokenPort.TokenPair tokenPair = new TokenPort.TokenPair(
                "access-token",
                "refresh-token",
                Instant.now().plusSeconds(900),
                Instant.now().plusSeconds(86_400),
                List.of("PROJECT_READ")
        );

        when(userAccountPort.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHasherPort.matches("password123", "hashed")).thenReturn(true);
        when(tokenPort.issue(user)).thenReturn(tokenPair);
        when(refreshTokenPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponse response = authApplicationService.login(new LoginRequest("  USER@EXAMPLE.COM  ", "password123"));

        verify(userAccountPort).findByEmail("user@example.com");
        assertThat(response.accessToken()).isEqualTo("access-token");
    }
}
