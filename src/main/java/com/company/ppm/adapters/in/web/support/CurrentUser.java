package com.company.ppm.adapters.in.web.support;

import com.company.ppm.domain.exception.AuthenticationFailedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.UUID;

public record CurrentUser(UUID userId, Set<String> authorities) {

    public static CurrentUser from(Authentication authentication) {
        UUID userId;
        try {
            userId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationFailedException("Invalid token subject");
        }
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new CurrentUser(userId, authorities);
    }
}
