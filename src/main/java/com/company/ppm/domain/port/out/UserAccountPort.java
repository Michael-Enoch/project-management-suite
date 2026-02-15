package com.company.ppm.domain.port.out;

import com.company.ppm.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountPort {
    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(UUID userId);

    UserAccount save(UserAccount userAccount);
}
