package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.Task;
import com.company.ppm.domain.model.TaskStatus;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TaskPort {
    Task save(Task task);

    Optional<Task> findById(UUID taskId);

    long countByProjectAndStatus(UUID projectId, TaskStatus status);

    long countOverdueOpenTasks(UUID projectId, LocalDate referenceDate);
}
