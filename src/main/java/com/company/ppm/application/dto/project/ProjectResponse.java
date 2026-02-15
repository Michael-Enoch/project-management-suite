package com.company.ppm.application.dto.project;

import com.company.ppm.domain.model.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String code,
        String name,
        String description,
        ProjectStatus status,
        UUID ownerUserId,
        LocalDate startDate,
        LocalDate endDate,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}
