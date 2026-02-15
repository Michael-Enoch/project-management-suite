package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.analytics.PortfolioReportResponse;
import com.company.ppm.application.dto.analytics.ProjectKpiResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface AnalyticsUseCase {
    ProjectKpiResponse getProjectKpi(UUID actorUserId, UUID projectId, LocalDate fromDate, LocalDate toDate);

    PortfolioReportResponse getPortfolioReport(UUID actorUserId, LocalDate fromDate, LocalDate toDate);
}
