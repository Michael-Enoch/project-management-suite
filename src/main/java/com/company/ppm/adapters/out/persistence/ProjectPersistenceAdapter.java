package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.ProjectEntity;
import com.company.ppm.adapters.out.persistence.entity.ProjectMemberEntity;
import com.company.ppm.adapters.out.persistence.entity.ProjectMemberId;
import com.company.ppm.adapters.out.persistence.repository.ProjectJpaRepository;
import com.company.ppm.adapters.out.persistence.repository.ProjectMemberJpaRepository;
import com.company.ppm.domain.model.MembershipRole;
import com.company.ppm.domain.model.Project;
import com.company.ppm.domain.model.ProjectStatus;
import com.company.ppm.domain.port.out.ProjectPort;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ProjectPersistenceAdapter implements ProjectPort {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMemberJpaRepository projectMemberJpaRepository;

    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository, ProjectMemberJpaRepository projectMemberJpaRepository) {
        this.projectJpaRepository = projectJpaRepository;
        this.projectMemberJpaRepository = projectMemberJpaRepository;
    }

    @Override
    @Transactional
    public Project save(Project project) {
        ProjectEntity entity = projectJpaRepository.findById(project.id()).orElseGet(ProjectEntity::new);
        if (entity.getId() != null && entity.getVersion() != project.version()) {
            throw new OptimisticLockingFailureException("Project version conflict");
        }

        entity.setId(project.id());
        entity.setCode(project.code());
        entity.setName(project.name());
        entity.setDescription(project.description());
        entity.setStatus(project.status());
        entity.setOwnerUserId(project.ownerUserId());
        entity.setStartDate(project.startDate());
        entity.setEndDate(project.endDate());
        entity.setCreatedAt(project.createdAt());
        entity.setUpdatedAt(project.updatedAt());

        ProjectEntity saved = projectJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Project> findById(UUID projectId) {
        return projectJpaRepository.findById(projectId).map(this::toDomain);
    }

    @Override
    public Optional<Project> findByCode(String code) {
        return projectJpaRepository.findByCodeIgnoreCase(code).map(this::toDomain);
    }

    @Override
    public Page<Project> findAccessibleProjects(UUID userId, boolean admin, ProjectStatus status, String query, Pageable pageable) {
        return projectJpaRepository.findAccessibleProjects(userId, admin, status, query, pageable).map(this::toDomain);
    }

    @Override
    public boolean existsMembership(UUID projectId, UUID userId) {
        return projectMemberJpaRepository.existsByIdProjectIdAndIdUserId(projectId, userId);
    }

    @Override
    @Transactional
    public void addMembership(UUID projectId, UUID userId, MembershipRole membershipRole) {
        ProjectMemberId id = new ProjectMemberId(projectId, userId);
        if (projectMemberJpaRepository.existsById(id)) {
            return;
        }
        ProjectMemberEntity projectMemberEntity = new ProjectMemberEntity();
        projectMemberEntity.setId(id);
        projectMemberEntity.setMembershipRole(membershipRole);
        projectMemberEntity.setCreatedAt(Instant.now());
        projectMemberJpaRepository.save(projectMemberEntity);
    }

    @Override
    public long countProjects(UUID userId, boolean admin, ProjectStatus status) {
        return projectJpaRepository.countAccessibleProjects(userId, admin, status);
    }

    @Override
    public long countActiveProjects(UUID userId, boolean admin) {
        return projectJpaRepository.countAccessibleProjects(userId, admin, ProjectStatus.ACTIVE);
    }

    @Override
    public double averageCompletionRate(UUID userId, boolean admin) {
        Double value = projectJpaRepository.averageCompletionRate(userId, admin);
        return value == null ? 0.0 : value;
    }

    private Project toDomain(ProjectEntity entity) {
        return new Project(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getOwnerUserId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
