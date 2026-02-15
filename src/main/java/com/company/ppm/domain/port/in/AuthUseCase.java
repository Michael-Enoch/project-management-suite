package com.company.ppm.domain.port.in;

import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.RefreshRequest;
import com.company.ppm.application.dto.auth.TokenResponse;

public interface AuthUseCase {
    TokenResponse login(LoginRequest request);

    TokenResponse refresh(RefreshRequest request);
}
