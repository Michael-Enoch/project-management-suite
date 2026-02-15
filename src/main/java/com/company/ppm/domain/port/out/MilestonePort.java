package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.Milestone;

public interface MilestonePort {
    Milestone save(Milestone milestone);
}
