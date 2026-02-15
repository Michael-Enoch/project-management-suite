package com.company.ppm.application.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank
        @Size(max = 200)
        String title,
        @Size(max = 4000)
        String description,
        UUID assigneeUserId,
        LocalDate dueDate
) {
}
