package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.MembershipRole;
import com.company.ppm.domain.model.Project;
import com.company.ppm.domain.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProjectPort {
    Project save(Project project);

    Optional<Project> findById(UUID projectId);

    Optional<Project> findByCode(String code);

    Page<Project> findAccessibleProjects(UUID userId, boolean admin, Pageable pageable);

    boolean existsMembership(UUID projectId, UUID userId);

    void addMembership(UUID projectId, UUID userId, MembershipRole membershipRole);

    long countProjects(UUID userId, boolean admin, ProjectStatus status);

    long countActiveProjects(UUID userId, boolean admin);

    double averageCompletionRate(UUID userId, boolean admin);
}
