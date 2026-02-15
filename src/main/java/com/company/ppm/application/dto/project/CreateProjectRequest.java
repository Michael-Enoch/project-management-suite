package com.company.ppm.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProjectRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Z0-9_-]+$")
        @Size(max = 24)
        String code,
        @NotBlank
        @Size(max = 140)
        String name,
        @Size(max = 2000)
        String description,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}
