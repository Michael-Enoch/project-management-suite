package com.company.ppm.domain.model;

import com.company.ppm.domain.exception.DomainException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Milestone(
        UUID id,
        UUID projectId,
        String title,
        LocalDate targetDate,
        boolean achieved,
        Instant achievedAt,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public Milestone {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(projectId, "projectId is required");
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(targetDate, "targetDate is required");
        if (title.isBlank()) {
            throw new DomainException("Milestone title is required");
        }
    }
}
