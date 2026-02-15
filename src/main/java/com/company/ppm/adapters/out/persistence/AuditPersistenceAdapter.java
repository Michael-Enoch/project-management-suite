package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.AuditEventEntity;
import com.company.ppm.adapters.out.persistence.repository.AuditEventJpaRepository;
import com.company.ppm.domain.model.AuditEvent;
import com.company.ppm.domain.port.out.AuditPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class AuditPersistenceAdapter implements AuditPort {

    private final AuditEventJpaRepository auditEventJpaRepository;

    public AuditPersistenceAdapter(AuditEventJpaRepository auditEventJpaRepository) {
        this.auditEventJpaRepository = auditEventJpaRepository;
    }

    @Override
    @Transactional
    public void record(AuditEvent auditEvent) {
        AuditEventEntity entity = new AuditEventEntity();
        entity.setActorUserId(auditEvent.actorUserId());
        entity.setAction(auditEvent.action());
        entity.setEntityType(auditEvent.entityType());
        entity.setEntityId(auditEvent.entityId());
        entity.setDetails(auditEvent.details());
        entity.setCreatedAt(auditEvent.createdAt());
        auditEventJpaRepository.save(entity);
    }
}
