package com.company.ppm.application.dto.auth;

import java.time.Instant;
import java.util.List;

public record TokenResponse(
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        List<String> permissions
) {
}
