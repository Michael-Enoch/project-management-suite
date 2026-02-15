package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.MilestoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MilestoneJpaRepository extends JpaRepository<MilestoneEntity, UUID> {
}
