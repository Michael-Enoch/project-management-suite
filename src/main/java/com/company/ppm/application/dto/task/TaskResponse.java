package com.company.ppm.application.dto.task;

import com.company.ppm.domain.model.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        UUID assigneeUserId,
        TaskStatus status,
        LocalDate dueDate,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}
