package com.company.ppm.adapters.in.web;

import com.company.ppm.adapters.in.web.support.CurrentUser;
import com.company.ppm.application.dto.milestone.CreateMilestoneRequest;
import com.company.ppm.application.dto.milestone.MilestoneResponse;
import com.company.ppm.domain.port.in.MilestoneUseCase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/milestones")
public class MilestoneController {

    private final MilestoneUseCase milestoneUseCase;

    public MilestoneController(MilestoneUseCase milestoneUseCase) {
        this.milestoneUseCase = milestoneUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MILESTONE_WRITE')")
    public MilestoneResponse create(
            Authentication authentication,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateMilestoneRequest request
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return milestoneUseCase.create(user.userId(), projectId, request);
    }
}
