package com.company.ppm.application.dto.project;

import com.company.ppm.domain.model.ProjectStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectSummaryResponse(
        UUID id,
        String code,
        String name,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate
) {
}
