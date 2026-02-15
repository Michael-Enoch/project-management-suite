package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.AuditEvent;

public interface AuditPort {
    void record(AuditEvent auditEvent);
}
