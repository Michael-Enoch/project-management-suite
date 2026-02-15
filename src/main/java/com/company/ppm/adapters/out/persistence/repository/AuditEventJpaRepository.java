package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventJpaRepository extends JpaRepository<AuditEventEntity, Long> {
}
