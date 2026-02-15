package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.task.CreateTaskRequest;
import com.company.ppm.application.dto.task.TaskResponse;
import com.company.ppm.application.dto.task.UpdateTaskRequest;

import java.util.UUID;

public interface TaskUseCase {
    TaskResponse create(UUID actorUserId, UUID projectId, CreateTaskRequest request);

    TaskResponse update(UUID actorUserId, UUID taskId, UpdateTaskRequest request);
}
