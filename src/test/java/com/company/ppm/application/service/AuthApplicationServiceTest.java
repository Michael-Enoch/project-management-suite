package com.company.ppm.application.service;

import com.company.ppm.application.dto.auth.AuthMeResponse;
import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.TokenResponse;
import com.company.ppm.domain.exception.AuthenticationFailedException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void meReturnsNormalizedProfilePayload() {
        UUID userId = UUID.randomUUID();
        UserAccount user = new UserAccount(
                userId,
                "user@example.com",
                "hashed",
                true,
                Set.of(RoleName.PROJECT_MANAGER, RoleName.TEAM_MEMBER),
                Set.of(PermissionName.TASK_READ, PermissionName.PROJECT_WRITE),
                Instant.now()
        );

        when(userAccountPort.findById(userId)).thenReturn(Optional.of(user));

        AuthMeResponse response = authApplicationService.me(userId);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.active()).isTrue();
        assertThat(response.roles()).containsExactly("PROJECT_MANAGER", "TEAM_MEMBER");
        assertThat(response.permissions()).containsExactly("PROJECT_WRITE", "TASK_READ");
    }

    @Test
    void meRejectsMissingOrInactiveUser() {
        UUID missingUserId = UUID.randomUUID();
        UUID inactiveUserId = UUID.randomUUID();
        UserAccount inactive = new UserAccount(
                inactiveUserId,
                "inactive@example.com",
                "hash",
                false,
                Set.of(RoleName.VIEWER),
                Set.of(PermissionName.PROJECT_READ),
                Instant.now()
        );

        when(userAccountPort.findById(missingUserId)).thenReturn(Optional.empty());
        when(userAccountPort.findById(inactiveUserId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> authApplicationService.me(missingUserId))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid user");
        assertThatThrownBy(() -> authApplicationService.me(inactiveUserId))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid user");
    }
}
