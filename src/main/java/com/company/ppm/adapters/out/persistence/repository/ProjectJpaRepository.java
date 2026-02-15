package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.ProjectEntity;
import com.company.ppm.domain.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {

    Optional<ProjectEntity> findByCodeIgnoreCase(String code);

    @Query("""
            select p from ProjectEntity p
            where (:admin = true or exists (
                select 1 from ProjectMemberEntity pm
                where pm.id.projectId = p.id and pm.id.userId = :userId
            ))
            """)
    Page<ProjectEntity> findAccessibleProjects(@Param("userId") UUID userId, @Param("admin") boolean admin, Pageable pageable);

    @Query("""
            select count(p) from ProjectEntity p
            where (:admin = true or exists (
                select 1 from ProjectMemberEntity pm
                where pm.id.projectId = p.id and pm.id.userId = :userId
            ))
            and (:status is null or p.status = :status)
            """)
    long countAccessibleProjects(@Param("userId") UUID userId, @Param("admin") boolean admin, @Param("status") ProjectStatus status);

    @Query(value = """
            select avg(
                case
                    when x.total_tasks = 0 then 0
                    else (x.done_tasks::decimal * 100.0 / x.total_tasks)
                end
            )
            from (
                select p.id as project_id,
                       count(t.id) as total_tasks,
                       count(t.id) filter (where t.status = 'DONE') as done_tasks
                from project p
                left join task_item t on t.project_id = p.id
                where (:admin = true
                    or exists (
                        select 1 from project_member pm
                        where pm.project_id = p.id and pm.user_id = :userId
                    ))
                group by p.id
            ) x
            """, nativeQuery = true)
    Double averageCompletionRate(@Param("userId") UUID userId, @Param("admin") boolean admin);
}
