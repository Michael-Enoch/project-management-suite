package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.MilestoneEntity;
import com.company.ppm.adapters.out.persistence.repository.MilestoneJpaRepository;
import com.company.ppm.domain.model.Milestone;
import com.company.ppm.domain.port.out.MilestonePort;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class MilestonePersistenceAdapter implements MilestonePort {

    private final MilestoneJpaRepository milestoneJpaRepository;

    public MilestonePersistenceAdapter(MilestoneJpaRepository milestoneJpaRepository) {
        this.milestoneJpaRepository = milestoneJpaRepository;
    }

    @Override
    @Transactional
    public Milestone save(Milestone milestone) {
        MilestoneEntity entity = milestoneJpaRepository.findById(milestone.id()).orElseGet(MilestoneEntity::new);
        if (entity.getId() != null && entity.getVersion() != milestone.version()) {
            throw new OptimisticLockingFailureException("Milestone version conflict");
        }

        entity.setId(milestone.id());
        entity.setProjectId(milestone.projectId());
        entity.setTitle(milestone.title());
        entity.setTargetDate(milestone.targetDate());
        entity.setAchieved(milestone.achieved());
        entity.setAchievedAt(milestone.achievedAt());
        entity.setCreatedAt(milestone.createdAt());
        entity.setUpdatedAt(milestone.updatedAt());

        MilestoneEntity saved = milestoneJpaRepository.save(entity);
        return new Milestone(
                saved.getId(),
                saved.getProjectId(),
                saved.getTitle(),
                saved.getTargetDate(),
                saved.isAchieved(),
                saved.getAchievedAt(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getVersion()
        );
    }
}
