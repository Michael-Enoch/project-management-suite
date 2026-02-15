package com.company.ppm.domain.model;

import java.time.LocalDate;

public record PortfolioReport(
        LocalDate fromDate,
        LocalDate toDate,
        long totalProjects,
        long activeProjects,
        double averageCompletionRate
) {
}
