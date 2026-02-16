package com.company.ppm.application.service;

import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.task.CreateTaskRequest;
import com.company.ppm.application.dto.task.TaskResponse;
import com.company.ppm.application.dto.task.UpdateTaskRequest;
import com.company.ppm.domain.exception.ConflictException;
import com.company.ppm.domain.exception.DomainException;
import com.company.ppm.domain.exception.ResourceNotFoundException;
import com.company.ppm.domain.model.AuditEvent;
import com.company.ppm.domain.model.Task;
import com.company.ppm.domain.model.TaskStatus;
import com.company.ppm.domain.port.in.TaskUseCase;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.ProjectPort;
import com.company.ppm.domain.port.out.TaskPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskApplicationService implements TaskUseCase {

    private final TaskPort taskPort;
    private final ProjectPort projectPort;
    private final UserAccountPort userAccountPort;
    private final AuthorizationService authorizationService;
    private final AuditPort auditPort;

    public TaskApplicationService(
            TaskPort taskPort,
            ProjectPort projectPort,
            UserAccountPort userAccountPort,
            AuthorizationService authorizationService,
            AuditPort auditPort
    ) {
        this.taskPort = taskPort;
        this.projectPort = projectPort;
        this.userAccountPort = userAccountPort;
        this.authorizationService = authorizationService;
        this.auditPort = auditPort;
    }

    @Override
    @Transactional
    public TaskResponse create(UUID actorUserId, UUID projectId, CreateTaskRequest request) {
        authorizationService.assertProjectAccess(actorUserId, projectId);

        projectPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        validateAssignee(actorUserId, projectId, request.assigneeUserId());

        Instant now = Instant.now();
        Task task = new Task(
                UUID.randomUUID(),
                projectId,
                request.title().trim(),
                request.description() == null ? null : request.description().trim(),
                request.assigneeUserId(),
                TaskStatus.TODO,
                request.dueDate(),
                now,
                now,
                0
        );

        Task saved = taskPort.save(task);
        auditPort.record(new AuditEvent(
                actorUserId,
                "TASK_CREATED",
                "TASK",
                saved.id(),
                Map.of("projectId", projectId.toString(), "status", saved.status().name()),
                Instant.now()
        ));

        return toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse update(UUID actorUserId, UUID taskId, UpdateTaskRequest request) {
        Task existing = taskPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        authorizationService.assertProjectAccess(actorUserId, existing.projectId());

        if (!request.version().equals(existing.version())) {
            throw new ConflictException("Task version conflict");
        }

        UUID assigneeUserId = request.assigneeUserId() == null ? existing.assigneeUserId() : request.assigneeUserId();
        validateAssignee(actorUserId, existing.projectId(), assigneeUserId);

        Task updated = new Task(
                existing.id(),
                existing.projectId(),
                request.title() == null ? existing.title() : request.title().trim(),
                request.description() == null ? existing.description() : request.description().trim(),
                assigneeUserId,
                request.status() == null ? existing.status() : request.status(),
                request.dueDate() == null ? existing.dueDate() : request.dueDate(),
                existing.createdAt(),
                Instant.now(),
                existing.version()
        );

        Task saved = taskPort.save(updated);
        auditPort.record(new AuditEvent(
                actorUserId,
                "TASK_UPDATED",
                "TASK",
                saved.id(),
                Map.of("projectId", saved.projectId().toString(), "status", saved.status().name()),
                Instant.now()
        ));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> list(UUID actorUserId, int page, int size, UUID projectId, TaskStatus status, String query) {
        boolean admin = authorizationService.isAdmin(actorUserId);
        String sanitizedQuery = query == null || query.isBlank() ? null : query.trim();
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("createdAt").descending()
        );

        Page<Task> result = taskPort.findAccessibleTasks(actorUserId, admin, projectId, status, sanitizedQuery, pageRequest);
        return PageResponse.from(result, this::toResponse);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.id(),
                task.projectId(),
                task.title(),
                task.description(),
                task.assigneeUserId(),
                task.status(),
                task.dueDate(),
                task.version(),
                task.createdAt(),
                task.updatedAt()
        );
    }

    private void validateAssignee(UUID actorUserId, UUID projectId, UUID assigneeUserId) {
        if (assigneeUserId == null) {
            return;
        }
        userAccountPort.findById(assigneeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        if (authorizationService.isAdmin(actorUserId)) {
            return;
        }
        if (!projectPort.existsMembership(projectId, assigneeUserId)) {
            throw new DomainException("Assignee must be a project member");
        }
    }
}
