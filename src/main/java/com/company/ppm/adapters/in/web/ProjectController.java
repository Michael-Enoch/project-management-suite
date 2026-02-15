package com.company.ppm.adapters.in.web;

import com.company.ppm.adapters.in.web.support.CurrentUser;
import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.project.CreateProjectRequest;
import com.company.ppm.application.dto.project.ProjectResponse;
import com.company.ppm.application.dto.project.ProjectSummaryResponse;
import com.company.ppm.domain.port.in.ProjectUseCase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectUseCase projectUseCase;

    public ProjectController(ProjectUseCase projectUseCase) {
        this.projectUseCase = projectUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_WRITE')")
    public ProjectResponse create(Authentication authentication, @Valid @RequestBody CreateProjectRequest request) {
        CurrentUser user = CurrentUser.from(authentication);
        return projectUseCase.create(user.userId(), request);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    public ProjectResponse getById(Authentication authentication, @PathVariable UUID projectId) {
        CurrentUser user = CurrentUser.from(authentication);
        return projectUseCase.getById(user.userId(), projectId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    public PageResponse<ProjectSummaryResponse> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return projectUseCase.list(user.userId(), page, size);
    }
}
