package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.RefreshTokenEntity;
import com.company.ppm.adapters.out.persistence.repository.RefreshTokenJpaRepository;
import com.company.ppm.domain.model.RefreshTokenRecord;
import com.company.ppm.domain.port.out.RefreshTokenPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class RefreshTokenPersistenceAdapter implements RefreshTokenPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    @Transactional
    public RefreshTokenRecord save(RefreshTokenRecord refreshTokenRecord) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(refreshTokenRecord.id());
        entity.setUserId(refreshTokenRecord.userId());
        entity.setTokenHash(refreshTokenRecord.tokenHash());
        entity.setExpiresAt(refreshTokenRecord.expiresAt());
        entity.setRevoked(refreshTokenRecord.revoked());
        entity.setCreatedAt(refreshTokenRecord.createdAt());
        RefreshTokenEntity saved = refreshTokenJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshTokenRecord> findActiveByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, Instant.now())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void revoke(UUID tokenId) {
        refreshTokenJpaRepository.findById(tokenId).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenJpaRepository.save(entity);
        });
    }

    private RefreshTokenRecord toDomain(RefreshTokenEntity entity) {
        return new RefreshTokenRecord(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }
}
