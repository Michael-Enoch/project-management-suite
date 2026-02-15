package com.company.ppm.application.dto.analytics;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectKpiResponse(
        UUID projectId,
        LocalDate fromDate,
        LocalDate toDate,
        long openTasks,
        long doneTasks,
        long overdueTasks,
        double completionRate
) {
}
