package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.milestone.CreateMilestoneRequest;
import com.company.ppm.application.dto.milestone.MilestoneResponse;

import java.util.UUID;

public interface MilestoneUseCase {
    MilestoneResponse create(UUID actorUserId, UUID projectId, CreateMilestoneRequest request);
}
