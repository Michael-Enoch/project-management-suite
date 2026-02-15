package com.company.ppm.adapters.out.security;

import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.TokenPort;
import com.company.ppm.infrastructure.security.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Component
public class JwtTokenAdapter implements TokenPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenAdapter(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public TokenPair issue(UserAccount userAccount) {
        Instant issuedAt = Instant.now();
        Instant accessTokenExpiresAt = issuedAt.plus(jwtProperties.getAccessTokenTtl());
        Instant refreshTokenExpiresAt = issuedAt.plus(jwtProperties.getRefreshTokenTtl());

        List<String> permissions = userAccount.permissions().stream()
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList();

        List<String> roles = userAccount.roles().stream()
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiresAt(accessTokenExpiresAt)
                .subject(userAccount.id().toString())
                .claim("email", userAccount.email())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .build();

        String accessToken = jwtEncoder.encode(
                        JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        return new TokenPair(
                accessToken,
                generateRefreshToken(),
                accessTokenExpiresAt,
                refreshTokenExpiresAt,
                permissions
        );
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
