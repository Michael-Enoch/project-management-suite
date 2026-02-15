package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.TaskEntity;
import com.company.ppm.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {
    long countByProjectIdAndStatus(UUID projectId, TaskStatus status);

    @Query("""
            select count(t) from TaskEntity t
            where t.projectId = :projectId
            and t.status <> com.company.ppm.domain.model.TaskStatus.DONE
            and t.dueDate is not null
            and t.dueDate < :referenceDate
            """)
    long countOverdueOpenTasks(@Param("projectId") UUID projectId, @Param("referenceDate") LocalDate referenceDate);
}
