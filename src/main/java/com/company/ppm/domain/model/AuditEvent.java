package com.company.ppm.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID actorUserId,
        String action,
        String entityType,
        UUID entityId,
        Map<String, Object> details,
        Instant createdAt
) {
}
