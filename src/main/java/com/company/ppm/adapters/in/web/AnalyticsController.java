package com.company.ppm.adapters.in.web;

import com.company.ppm.adapters.in.web.support.CurrentUser;
import com.company.ppm.application.dto.analytics.PortfolioReportResponse;
import com.company.ppm.application.dto.analytics.ProjectKpiResponse;
import com.company.ppm.domain.port.in.AnalyticsUseCase;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AnalyticsController {

    private final AnalyticsUseCase analyticsUseCase;

    public AnalyticsController(AnalyticsUseCase analyticsUseCase) {
        this.analyticsUseCase = analyticsUseCase;
    }

    @GetMapping("/kpis/projects/{projectId}")
    @PreAuthorize("hasAuthority('KPI_READ')")
    public ProjectKpiResponse projectKpi(
            Authentication authentication,
            @PathVariable UUID projectId,
            @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return analyticsUseCase.getProjectKpi(user.userId(), projectId, fromDate, toDate);
    }

    @GetMapping("/reports/portfolio-performance")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public PortfolioReportResponse portfolioReport(
            Authentication authentication,
            @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return analyticsUseCase.getPortfolioReport(user.userId(), fromDate, toDate);
    }
}
