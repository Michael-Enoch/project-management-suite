package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.TaskEntity;
import com.company.ppm.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("""
            select t from TaskEntity t
            where (:admin = true or exists (
                select 1 from ProjectMemberEntity pm
                where pm.id.projectId = t.projectId and pm.id.userId = :userId
            ))
            and (:projectId is null or t.projectId = :projectId)
            and (:status is null or t.status = :status)
            and (
                :query is null
                or lower(t.title) like lower(concat('%', :query, '%'))
                or lower(coalesce(t.description, '')) like lower(concat('%', :query, '%'))
            )
            """)
    Page<TaskEntity> findAccessibleTasks(
            @Param("userId") UUID userId,
            @Param("admin") boolean admin,
            @Param("projectId") UUID projectId,
            @Param("status") TaskStatus status,
            @Param("query") String query,
            Pageable pageable
    );

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
