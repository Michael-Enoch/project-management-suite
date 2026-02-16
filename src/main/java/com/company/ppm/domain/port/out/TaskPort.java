package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.Task;
import com.company.ppm.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TaskPort {
    Task save(Task task);

    Optional<Task> findById(UUID taskId);

    Page<Task> findAccessibleTasks(UUID userId, boolean admin, UUID projectId, TaskStatus status, String query, Pageable pageable);

    long countByProjectAndStatus(UUID projectId, TaskStatus status);

    long countOverdueOpenTasks(UUID projectId, LocalDate referenceDate);
}
