package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.ProjectMemberEntity;
import com.company.ppm.adapters.out.persistence.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberId> {
    boolean existsByIdProjectIdAndIdUserId(UUID projectId, UUID userId);
}
