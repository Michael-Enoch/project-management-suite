package com.company.ppm.application.service;

import com.company.ppm.application.dto.task.CreateTaskRequest;
import com.company.ppm.application.dto.task.TaskResponse;
import com.company.ppm.application.dto.task.UpdateTaskRequest;
import com.company.ppm.domain.exception.DomainException;
import com.company.ppm.domain.model.Project;
import com.company.ppm.domain.model.ProjectStatus;
import com.company.ppm.domain.model.Task;
import com.company.ppm.domain.model.TaskStatus;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.ProjectPort;
import com.company.ppm.domain.port.out.TaskPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskApplicationServiceTest {

    @Mock
    private TaskPort taskPort;
    @Mock
    private ProjectPort projectPort;
    @Mock
    private UserAccountPort userAccountPort;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private TaskApplicationService taskApplicationService;

    @Test
    void createRejectsAssigneeOutsideProjectMembership() {
        UUID actorUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID assigneeUserId = UUID.randomUUID();

        when(projectPort.findById(projectId)).thenReturn(Optional.of(project(projectId, actorUserId)));
        when(userAccountPort.findById(assigneeUserId)).thenReturn(Optional.of(user(assigneeUserId)));
        when(projectPort.existsMembership(projectId, assigneeUserId)).thenReturn(false);

        CreateTaskRequest request = new CreateTaskRequest("Roadmap", "Build roadmap", assigneeUserId, LocalDate.now().plusDays(7));

        assertThatThrownBy(() -> taskApplicationService.create(actorUserId, projectId, request))
                .isInstanceOf(DomainException.class)
                .hasMessage("Assignee must be a project member");

        verify(taskPort, never()).save(any(Task.class));
    }

    @Test
    void updateRejectsAssigneeOutsideProjectMembership() {
        UUID actorUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID assigneeUserId = UUID.randomUUID();

        Task existing = new Task(
                taskId,
                projectId,
                "Initial task",
                null,
                null,
                TaskStatus.TODO,
                null,
                Instant.now(),
                Instant.now(),
                3
        );
        when(taskPort.findById(taskId)).thenReturn(Optional.of(existing));
        when(userAccountPort.findById(assigneeUserId)).thenReturn(Optional.of(user(assigneeUserId)));
        when(projectPort.existsMembership(projectId, assigneeUserId)).thenReturn(false);

        UpdateTaskRequest request = new UpdateTaskRequest(existing.version(), null, null, assigneeUserId, null, null);

        assertThatThrownBy(() -> taskApplicationService.update(actorUserId, taskId, request))
                .isInstanceOf(DomainException.class)
                .hasMessage("Assignee must be a project member");

        verify(taskPort, never()).save(any(Task.class));
    }

    @Test
    void createAllowsAssigneeWhoIsProjectMember() {
        UUID actorUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID assigneeUserId = UUID.randomUUID();

        when(projectPort.findById(projectId)).thenReturn(Optional.of(project(projectId, actorUserId)));
        when(userAccountPort.findById(assigneeUserId)).thenReturn(Optional.of(user(assigneeUserId)));
        when(projectPort.existsMembership(projectId, assigneeUserId)).thenReturn(true);
        when(taskPort.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskRequest request = new CreateTaskRequest("Roadmap", "Build roadmap", assigneeUserId, LocalDate.now().plusDays(7));

        TaskResponse response = taskApplicationService.create(actorUserId, projectId, request);

        assertThat(response.assigneeUserId()).isEqualTo(assigneeUserId);
        assertThat(response.projectId()).isEqualTo(projectId);
        verify(taskPort).save(any(Task.class));
        verify(auditPort).record(any());
    }

    @Test
    void createAllowsAssigneeOutsideProjectMembershipForAdmin() {
        UUID actorUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID assigneeUserId = UUID.randomUUID();

        when(projectPort.findById(projectId)).thenReturn(Optional.of(project(projectId, actorUserId)));
        when(userAccountPort.findById(assigneeUserId)).thenReturn(Optional.of(user(assigneeUserId)));
        when(authorizationService.isAdmin(actorUserId)).thenReturn(true);
        when(taskPort.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskRequest request = new CreateTaskRequest("Roadmap", "Build roadmap", assigneeUserId, LocalDate.now().plusDays(7));

        TaskResponse response = taskApplicationService.create(actorUserId, projectId, request);

        assertThat(response.assigneeUserId()).isEqualTo(assigneeUserId);
        verify(taskPort).save(any(Task.class));
        verify(auditPort).record(any());
    }

    private static Project project(UUID projectId, UUID ownerUserId) {
        return new Project(
                projectId,
                "PPM-TEST",
                "PPM",
                null,
                ProjectStatus.ACTIVE,
                ownerUserId,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                Instant.now(),
                Instant.now(),
                0
        );
    }

    private static UserAccount user(UUID userId) {
        return new UserAccount(
                userId,
                "member@example.com",
                "hash",
                true,
                Set.of(),
                Set.of(),
                Instant.now()
        );
    }
}
