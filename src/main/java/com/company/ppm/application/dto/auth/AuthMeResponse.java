package com.company.ppm.application.dto.auth;

import java.util.List;
import java.util.UUID;

public record AuthMeResponse(
        UUID id,
        String email,
        boolean active,
        List<String> roles,
        List<String> permissions
) {
}
