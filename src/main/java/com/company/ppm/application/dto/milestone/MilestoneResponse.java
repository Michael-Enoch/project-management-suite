package com.company.ppm.application.dto.milestone;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MilestoneResponse(
        UUID id,
        UUID projectId,
        String title,
        LocalDate targetDate,
        boolean achieved,
        Instant achievedAt,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}
