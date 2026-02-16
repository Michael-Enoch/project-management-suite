package com.company.ppm.application.service;

import com.company.ppm.domain.model.Project;
import com.company.ppm.domain.model.ProjectStatus;
import com.company.ppm.domain.port.out.AuditPort;
import com.company.ppm.domain.port.out.ProjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectApplicationServiceTest {

    @Mock
    private ProjectPort projectPort;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private ProjectApplicationService projectApplicationService;

    @Test
    void listAppliesFiltersAndSearch() {
        UUID actorUserId = UUID.randomUUID();
        Project project = new Project(
                UUID.randomUUID(),
                "PPM-101",
                "Alpha Program",
                "Alpha rollout",
                ProjectStatus.ACTIVE,
                actorUserId,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                Instant.now().minusSeconds(3600),
                Instant.now(),
                0
        );
        Page<Project> page = new PageImpl<>(List.of(project), PageRequest.of(0, 20), 1);

        when(authorizationService.isAdmin(actorUserId)).thenReturn(false);
        when(projectPort.findAccessibleProjects(eq(actorUserId), eq(false), eq(ProjectStatus.ACTIVE), eq("alpha"), any(PageRequest.class)))
                .thenReturn(page);

        var response = projectApplicationService.list(actorUserId, 0, 20, ProjectStatus.ACTIVE, "  alpha ");

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).code()).isEqualTo("PPM-101");
    }
}
