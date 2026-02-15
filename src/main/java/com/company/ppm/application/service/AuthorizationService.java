package com.company.ppm.application.service;

import com.company.ppm.domain.exception.ForbiddenOperationException;
import com.company.ppm.domain.model.PermissionName;
import com.company.ppm.domain.port.out.ProjectPort;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthorizationService {

    private final UserAccountPort userAccountPort;
    private final ProjectPort projectPort;

    public AuthorizationService(UserAccountPort userAccountPort, ProjectPort projectPort) {
        this.userAccountPort = userAccountPort;
        this.projectPort = projectPort;
    }

    public boolean isAdmin(UUID userId) {
        return userAccountPort.findById(userId)
                .map(user -> user.permissions().contains(PermissionName.USER_ADMIN))
                .orElse(false);
    }

    public void assertProjectAccess(UUID userId, UUID projectId) {
        if (isAdmin(userId)) {
            return;
        }
        if (!projectPort.existsMembership(projectId, userId)) {
            throw new ForbiddenOperationException("Access denied for project");
        }
    }
}
