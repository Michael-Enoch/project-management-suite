package com.company.ppm.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserAccount(
        UUID id,
        String email,
        String passwordHash,
        boolean active,
        Set<RoleName> roles,
        Set<PermissionName> permissions,
        Instant createdAt
) {
}
