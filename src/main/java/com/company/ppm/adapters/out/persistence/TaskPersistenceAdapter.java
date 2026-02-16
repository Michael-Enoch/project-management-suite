package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.TaskEntity;
import com.company.ppm.adapters.out.persistence.repository.TaskJpaRepository;
import com.company.ppm.domain.model.Task;
import com.company.ppm.domain.model.TaskStatus;
import com.company.ppm.domain.port.out.TaskPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class TaskPersistenceAdapter implements TaskPort {

    private final TaskJpaRepository taskJpaRepository;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
    }

    @Override
    @Transactional
    public Task save(Task task) {
        TaskEntity entity = taskJpaRepository.findById(task.id()).orElseGet(TaskEntity::new);
        if (entity.getId() != null && entity.getVersion() != task.version()) {
            throw new OptimisticLockingFailureException("Task version conflict");
        }

        entity.setId(task.id());
        entity.setProjectId(task.projectId());
        entity.setTitle(task.title());
        entity.setDescription(task.description());
        entity.setAssigneeUserId(task.assigneeUserId());
        entity.setStatus(task.status());
        entity.setDueDate(task.dueDate());
        entity.setCreatedAt(task.createdAt());
        entity.setUpdatedAt(task.updatedAt());

        TaskEntity saved = taskJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Task> findById(UUID taskId) {
        return taskJpaRepository.findById(taskId).map(this::toDomain);
    }

    @Override
    public Page<Task> findAccessibleTasks(UUID userId, boolean admin, UUID projectId, TaskStatus status, String query, Pageable pageable) {
        return taskJpaRepository.findAccessibleTasks(userId, admin, projectId, status, query, pageable).map(this::toDomain);
    }

    @Override
    public long countByProjectAndStatus(UUID projectId, TaskStatus status) {
        return taskJpaRepository.countByProjectIdAndStatus(projectId, status);
    }

    @Override
    public long countOverdueOpenTasks(UUID projectId, LocalDate referenceDate) {
        return taskJpaRepository.countOverdueOpenTasks(projectId, referenceDate);
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getAssigneeUserId(),
                entity.getStatus(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
