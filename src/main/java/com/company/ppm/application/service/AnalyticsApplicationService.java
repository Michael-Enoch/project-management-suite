package com.company.ppm.application.service;

import com.company.ppm.application.dto.analytics.PortfolioReportResponse;
import com.company.ppm.application.dto.analytics.ProjectKpiResponse;
import com.company.ppm.domain.exception.DomainException;
import com.company.ppm.domain.model.ProjectStatus;
import com.company.ppm.domain.model.TaskStatus;
import com.company.ppm.domain.port.in.AnalyticsUseCase;
import com.company.ppm.domain.port.out.ProjectPort;
import com.company.ppm.domain.port.out.TaskPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AnalyticsApplicationService implements AnalyticsUseCase {

    private final ProjectPort projectPort;
    private final TaskPort taskPort;
    private final AuthorizationService authorizationService;

    public AnalyticsApplicationService(ProjectPort projectPort, TaskPort taskPort, AuthorizationService authorizationService) {
        this.projectPort = projectPort;
        this.taskPort = taskPort;
        this.authorizationService = authorizationService;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectKpiResponse getProjectKpi(UUID actorUserId, UUID projectId, LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);
        authorizationService.assertProjectAccess(actorUserId, projectId);

        long todo = taskPort.countByProjectAndStatus(projectId, TaskStatus.TODO);
        long inProgress = taskPort.countByProjectAndStatus(projectId, TaskStatus.IN_PROGRESS);
        long blocked = taskPort.countByProjectAndStatus(projectId, TaskStatus.BLOCKED);
        long done = taskPort.countByProjectAndStatus(projectId, TaskStatus.DONE);
        long open = todo + inProgress + blocked;
        long overdue = taskPort.countOverdueOpenTasks(projectId, toDate);
        long total = open + done;
        double completionRate = total == 0 ? 0.0 : ((double) done * 100.0) / total;

        return new ProjectKpiResponse(projectId, fromDate, toDate, open, done, overdue, completionRate);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioReportResponse getPortfolioReport(UUID actorUserId, LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);
        boolean admin = authorizationService.isAdmin(actorUserId);
        long totalProjects = projectPort.countProjects(actorUserId, admin, null);
        long activeProjects = projectPort.countProjects(actorUserId, admin, ProjectStatus.ACTIVE);
        double averageCompletionRate = projectPort.averageCompletionRate(actorUserId, admin);

        return new PortfolioReportResponse(fromDate, toDate, totalProjects, activeProjects, averageCompletionRate);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (toDate.isBefore(fromDate)) {
            throw new DomainException("toDate cannot be before fromDate");
        }
    }
}
