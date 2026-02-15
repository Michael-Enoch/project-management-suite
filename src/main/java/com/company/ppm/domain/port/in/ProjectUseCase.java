package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.project.CreateProjectRequest;
import com.company.ppm.application.dto.project.ProjectResponse;
import com.company.ppm.application.dto.project.ProjectSummaryResponse;

import java.util.UUID;

public interface ProjectUseCase {
    ProjectResponse create(UUID actorUserId, CreateProjectRequest request);

    ProjectResponse getById(UUID actorUserId, UUID projectId);

    PageResponse<ProjectSummaryResponse> list(UUID actorUserId, int page, int size);
}
