package com.company.ppm.application.dto.analytics;

import java.time.LocalDate;

public record PortfolioReportResponse(
        LocalDate fromDate,
        LocalDate toDate,
        long totalProjects,
        long activeProjects,
        double averageCompletionRate
) {
}
