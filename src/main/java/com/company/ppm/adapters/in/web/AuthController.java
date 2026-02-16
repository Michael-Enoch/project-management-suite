package com.company.ppm.adapters.in.web;

import com.company.ppm.adapters.in.web.support.CurrentUser;
import com.company.ppm.application.dto.auth.AuthMeResponse;
import com.company.ppm.application.dto.auth.LoginRequest;
import com.company.ppm.application.dto.auth.RefreshRequest;
import com.company.ppm.application.dto.auth.TokenResponse;
import com.company.ppm.domain.port.in.AuthUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authUseCase.refresh(request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthMeResponse> me(Authentication authentication) {
        CurrentUser user = CurrentUser.from(authentication);
        return ResponseEntity.ok(authUseCase.me(user.userId()));
    }
}
