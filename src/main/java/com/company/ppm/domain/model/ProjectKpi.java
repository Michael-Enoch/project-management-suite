package com.company.ppm.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectKpi(
        UUID projectId,
        LocalDate fromDate,
        LocalDate toDate,
        long openTasks,
        long doneTasks,
        long overdueTasks,
        double completionRate
) {
}
