package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.auth.AuthMeResponse;
import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.RefreshRequest;
import com.company.ppm.application.dto.auth.TokenResponse;

import java.util.UUID;

public interface AuthUseCase {
    TokenResponse login(LoginRequest request);

    TokenResponse refresh(RefreshRequest request);

    AuthMeResponse me(UUID actorUserId);
}
