package com.company.ppm.domain.model;

import com.company.ppm.domain.exception.DomainException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Task(
        UUID id,
        UUID projectId,
        String title,
        String description,
        UUID assigneeUserId,
        TaskStatus status,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public Task {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(projectId, "projectId is required");
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(status, "status is required");
        if (title.isBlank()) {
            throw new DomainException("Task title is required");
        }
    }
}
