package com.company.ppm.domain.model;

import com.company.ppm.domain.exception.DomainException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Project(
        UUID id,
        String code,
        String name,
        String description,
        ProjectStatus status,
        UUID ownerUserId,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public Project {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(code, "code is required");
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(ownerUserId, "ownerUserId is required");
        Objects.requireNonNull(startDate, "startDate is required");
        Objects.requireNonNull(endDate, "endDate is required");

        if (code.isBlank() || name.isBlank()) {
            throw new DomainException("Project code and name are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new DomainException("Project endDate cannot be before startDate");
        }
    }
}
