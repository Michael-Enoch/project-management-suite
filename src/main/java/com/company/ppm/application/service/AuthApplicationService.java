package com.company.ppm.application.service;

import com.company.ppm.application.dto.auth.AuthMeResponse;
import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.RefreshRequest;
import com.company.ppm.application.dto.auth.TokenResponse;
import com.company.ppm.domain.exception.AuthenticationFailedException;
import com.company.ppm.domain.model.AuditEvent;
import com.company.ppm.domain.model.RefreshTokenRecord;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.in.AuthUseCase;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.PasswordHasherPort;
import com.company.ppm.domain.port.out.RefreshTokenPort;
import com.company.ppm.domain.port.out.TokenPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthApplicationService implements AuthUseCase {

    private final UserAccountPort userAccountPort;
    private final PasswordHasherPort passwordHasherPort;
    private final TokenPort tokenPort;
    private final RefreshTokenPort refreshTokenPort;
    private final AuditPort auditPort;

    public AuthApplicationService(
            UserAccountPort userAccountPort,
            PasswordHasherPort passwordHasherPort,
            TokenPort tokenPort,
            RefreshTokenPort refreshTokenPort,
            AuditPort auditPort
    ) {
        this.userAccountPort = userAccountPort;
        this.passwordHasherPort = passwordHasherPort;
        this.tokenPort = tokenPort;
        this.refreshTokenPort = refreshTokenPort;
        this.auditPort = auditPort;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(request.email()))
                .filter(UserAccount::active)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!passwordHasherPort.matches(request.password(), user.passwordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        TokenPort.TokenPair tokenPair = tokenPort.issue(user);
        saveRefreshToken(user.id(), tokenPair.refreshToken(), tokenPair.refreshTokenExpiresAt());

        auditPort.record(new AuditEvent(
                user.id(),
                "AUTH_LOGIN",
                "USER",
                user.id(),
                Map.of("email", user.email()),
                Instant.now()
        ));

        return toResponse(tokenPair);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken().trim();
        String tokenHash = hashToken(refreshToken);

        RefreshTokenRecord current = refreshTokenPort.findActiveByTokenHash(tokenHash)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid refresh token"));

        if (current.expiresAt().isBefore(Instant.now())) {
            refreshTokenPort.revoke(current.id());
            throw new AuthenticationFailedException("Refresh token expired");
        }

        UserAccount user = userAccountPort.findById(current.userId())
                .filter(UserAccount::active)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid refresh token"));

        refreshTokenPort.revoke(current.id());

        TokenPort.TokenPair newPair = tokenPort.issue(user);
        saveRefreshToken(user.id(), newPair.refreshToken(), newPair.refreshTokenExpiresAt());

        auditPort.record(new AuditEvent(
                user.id(),
                "AUTH_REFRESH",
                "USER",
                user.id(),
                Map.of(),
                Instant.now()
        ));

        return toResponse(newPair);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthMeResponse me(UUID actorUserId) {
        UserAccount user = userAccountPort.findById(actorUserId)
                .filter(UserAccount::active)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid user"));

        List<String> roles = user.roles() == null
                ? List.of()
                : user.roles().stream().map(Enum::name).sorted().toList();
        List<String> permissions = user.permissions() == null
                ? List.of()
                : user.permissions().stream().map(Enum::name).sorted().toList();

        return new AuthMeResponse(
                user.id(),
                user.email(),
                user.active(),
                roles,
                permissions
        );
    }

    private void saveRefreshToken(UUID userId, String rawToken, Instant expiresAt) {
        refreshTokenPort.save(new RefreshTokenRecord(
                UUID.randomUUID(),
                userId,
                hashToken(rawToken),
                expiresAt,
                false,
                Instant.now()
        ));
    }

    private static TokenResponse toResponse(TokenPort.TokenPair tokenPair) {
        return new TokenResponse(
                "Bearer",
                tokenPair.accessToken(),
                tokenPair.accessTokenExpiresAt(),
                tokenPair.refreshToken(),
                tokenPair.refreshTokenExpiresAt(),
                tokenPair.permissions()
        );
    }

    private static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
