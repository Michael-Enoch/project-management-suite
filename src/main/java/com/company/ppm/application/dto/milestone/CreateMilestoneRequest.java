package com.company.ppm.application.dto.milestone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMilestoneRequest(
        @NotBlank
        @Size(max = 180)
        String title,
        @NotNull
        LocalDate targetDate
) {
}
