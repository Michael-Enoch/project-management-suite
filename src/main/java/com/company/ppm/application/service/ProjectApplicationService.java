package com.company.ppm.application.service;

import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.project.CreateProjectRequest;
import com.company.ppm.application.dto.project.ProjectResponse;
import com.company.ppm.application.dto.project.ProjectSummaryResponse;
import com.company.ppm.domain.exception.ConflictException;
import com.company.ppm.domain.exception.ResourceNotFoundException;
import com.company.ppm.domain.model.AuditEvent;
import com.company.ppm.domain.model.MembershipRole;
import com.company.ppm.domain.model.Project;
import com.company.ppm.domain.model.ProjectStatus;
import com.company.ppm.domain.port.in.ProjectUseCase;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.ProjectPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectApplicationService implements ProjectUseCase {

    private final ProjectPort projectPort;
    private final AuthorizationService authorizationService;
    private final AuditPort auditPort;

    public ProjectApplicationService(ProjectPort projectPort, AuthorizationService authorizationService, AuditPort auditPort) {
        this.projectPort = projectPort;
        this.authorizationService = authorizationService;
        this.auditPort = auditPort;
    }

    @Override
    @Transactional
    public ProjectResponse create(UUID actorUserId, CreateProjectRequest request) {
        String code = request.code().trim().toUpperCase();
        if (projectPort.findByCode(code).isPresent()) {
            throw new ConflictException("Project code already exists");
        }

        Instant now = Instant.now();
        Project project = new Project(
                UUID.randomUUID(),
                code,
                request.name().trim(),
                request.description() == null ? null : request.description().trim(),
                ProjectStatus.DRAFT,
                actorUserId,
                request.startDate(),
                request.endDate(),
                now,
                now,
                0
        );

        Project saved = projectPort.save(project);
        projectPort.addMembership(saved.id(), actorUserId, MembershipRole.OWNER);

        auditPort.record(new AuditEvent(
                actorUserId,
                "PROJECT_CREATED",
                "PROJECT",
                saved.id(),
                Map.of("code", saved.code(), "status", saved.status().name()),
                Instant.now()
        ));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID actorUserId, UUID projectId) {
        authorizationService.assertProjectAccess(actorUserId, projectId);
        Project project = projectPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectSummaryResponse> list(UUID actorUserId, int page, int size) {
        boolean admin = authorizationService.isAdmin(actorUserId);
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("createdAt").descending());
        Page<Project> result = projectPort.findAccessibleProjects(actorUserId, admin, pageRequest);
        return PageResponse.from(result, this::toSummaryResponse);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.id(),
                project.code(),
                project.name(),
                project.description(),
                project.status(),
                project.ownerUserId(),
                project.startDate(),
                project.endDate(),
                project.version(),
                project.createdAt(),
                project.updatedAt()
        );
    }

    private ProjectSummaryResponse toSummaryResponse(Project project) {
        return new ProjectSummaryResponse(
                project.id(),
                project.code(),
                project.name(),
                project.status(),
                project.startDate(),
                project.endDate()
        );
    }
}
