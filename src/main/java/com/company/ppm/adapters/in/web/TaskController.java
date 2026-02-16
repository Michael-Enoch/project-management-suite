package com.company.ppm.adapters.in.web;

import com.company.ppm.adapters.in.web.support.CurrentUser;
import com.company.ppm.application.dto.common.PageResponse;
import com.company.ppm.application.dto.task.CreateTaskRequest;
import com.company.ppm.application.dto.task.TaskResponse;
import com.company.ppm.application.dto.task.UpdateTaskRequest;
import com.company.ppm.domain.model.TaskStatus;
import com.company.ppm.domain.port.in.TaskUseCase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskUseCase taskUseCase;

    public TaskController(TaskUseCase taskUseCase) {
        this.taskUseCase = taskUseCase;
    }

    @PostMapping("/projects/{projectId}/tasks")
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    public TaskResponse create(
            Authentication authentication,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return taskUseCase.create(user.userId(), projectId, request);
    }

    @PatchMapping("/tasks/{taskId}")
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    public TaskResponse update(
            Authentication authentication,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return taskUseCase.update(user.userId(), taskId, request);
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAuthority('TASK_READ')")
    public PageResponse<TaskResponse> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(name = "q", required = false) String query
    ) {
        CurrentUser user = CurrentUser.from(authentication);
        return taskUseCase.list(user.userId(), page, size, projectId, status, query);
    }
}
