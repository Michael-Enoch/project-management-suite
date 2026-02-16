package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.task.CreateTaskRequest;
import com.company.ppm.application.dto.task.TaskResponse;
import com.company.ppm.application.dto.task.UpdateTaskRequest;
import com.company.ppm.domain.model.TaskStatus;

import java.util.UUID;

public interface TaskUseCase {
    TaskResponse create(UUID actorUserId, UUID projectId, CreateTaskRequest request);

    TaskResponse update(UUID actorUserId, UUID taskId, UpdateTaskRequest request);

    PageResponse<TaskResponse> list(UUID actorUserId, int page, int size, UUID projectId, TaskStatus status, String query);
}
