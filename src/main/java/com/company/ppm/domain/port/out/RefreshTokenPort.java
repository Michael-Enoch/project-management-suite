package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.RefreshTokenRecord;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {
    RefreshTokenRecord save(RefreshTokenRecord refreshTokenRecord);

    Optional<RefreshTokenRecord> findActiveByTokenHash(String tokenHash);

    void revoke(UUID tokenId);
}
