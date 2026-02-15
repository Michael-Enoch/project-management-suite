package com.company.ppm.application.dto.task;

import com.company.ppm.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskRequest(
        @NotNull
        Long version,
        @Size(max = 200)
        String title,
        @Size(max = 4000)
        String description,
        UUID assigneeUserId,
        TaskStatus status,
        LocalDate dueDate
) {
}
