package com.company.ppm.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshRequest(
        @NotBlank
        @Size(max = 2000)
        String refreshToken
) {
}
