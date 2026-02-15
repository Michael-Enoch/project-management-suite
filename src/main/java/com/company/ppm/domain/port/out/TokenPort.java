package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.UserAccount;

import java.time.Instant;
import java.util.List;

public interface TokenPort {
    TokenPair issue(UserAccount userAccount);

    record TokenPair(
            String accessToken,
            String refreshToken,
            Instant accessTokenExpiresAt,
            Instant refreshTokenExpiresAt,
            List<String> permissions
    ) {
    }
}
