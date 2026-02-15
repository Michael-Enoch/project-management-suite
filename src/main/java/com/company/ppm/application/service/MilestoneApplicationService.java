package com.company.ppm.application.service;

import com.company.ppm.application.dto.milestone.CreateMilestoneRequest;
import com.company.ppm.application.dto.milestone.MilestoneResponse;
import com.company.ppm.domain.exception.ResourceNotFoundException;
import com.company.ppm.domain.model.AuditEvent;
import com.company.ppm.domain.model.Milestone;
import com.company.ppm.domain.port.in.MilestoneUseCase;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.MilestonePort;
import com.company.ppm.domain.port.out.ProjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class MilestoneApplicationService implements MilestoneUseCase {

    private final MilestonePort milestonePort;
    private final ProjectPort projectPort;
    private final AuthorizationService authorizationService;
    private final AuditPort auditPort;

    public MilestoneApplicationService(
            MilestonePort milestonePort,
            ProjectPort projectPort,
            AuthorizationService authorizationService,
            AuditPort auditPort
    ) {
        this.milestonePort = milestonePort;
        this.projectPort = projectPort;
        this.authorizationService = authorizationService;
        this.auditPort = auditPort;
    }

    @Override
    @Transactional
    public MilestoneResponse create(UUID actorUserId, UUID projectId, CreateMilestoneRequest request) {
        authorizationService.assertProjectAccess(actorUserId, projectId);
        projectPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Instant now = Instant.now();
        Milestone milestone = new Milestone(
                UUID.randomUUID(),
                projectId,
                request.title().trim(),
                request.targetDate(),
                false,
                null,
                now,
                now,
                0
        );

        Milestone saved = milestonePort.save(milestone);
        auditPort.record(new AuditEvent(
                actorUserId,
                "MILESTONE_CREATED",
                "MILESTONE",
                saved.id(),
                Map.of("projectId", projectId.toString()),
                Instant.now()
        ));

        return new MilestoneResponse(
                saved.id(),
                saved.projectId(),
                saved.title(),
                saved.targetDate(),
                saved.achieved(),
                saved.achievedAt(),
                saved.version(),
                saved.createdAt(),
                saved.updatedAt()
        );
    }
}
