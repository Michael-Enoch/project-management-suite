package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.RoleEntity;
import com.company.ppm.adapters.out.persistence.entity.UserEntity;
import com.company.ppm.adapters.out.persistence.repository.RoleJpaRepository;
import com.company.ppm.adapters.out.persistence.repository.UserJpaRepository;
import com.company.ppm.domain.exception.DomainException;
import com.company.ppm.domain.model.PermissionName;
import com.company.ppm.domain.model.RoleName;
import com.company.ppm.domain.model.UserAccount;
import com.company.ppm.domain.port.out.UserAccountPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class UserAccountPersistenceAdapter implements UserAccountPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public UserAccountPersistenceAdapter(UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(normalizeEmail(email)).map(this::toDomain);
    }

    @Override
    public Optional<UserAccount> findById(UUID userId) {
        return userJpaRepository.findById(userId).map(this::toDomain);
    }

    @Override
    @Transactional
    public UserAccount save(UserAccount userAccount) {
        UUID userId = userAccount.id() == null ? UUID.randomUUID() : userAccount.id();
        UserEntity entity = userJpaRepository.findById(userId).orElseGet(UserEntity::new);

        entity.setId(userId);
        entity.setEmail(normalizeEmail(userAccount.email()));
        entity.setPasswordHash(userAccount.passwordHash());
        entity.setActive(userAccount.active());
        entity.setCreatedAt(userAccount.createdAt() == null ? Instant.now() : userAccount.createdAt());

        Set<RoleName> roleNames = userAccount.roles() == null ? Set.of() : userAccount.roles();
        Set<RoleEntity> roleEntities = roleNames.isEmpty()
                ? new HashSet<>()
                : new HashSet<>(roleJpaRepository.findByNameIn(roleNames));
        if (roleEntities.size() != roleNames.size()) {
            throw new DomainException("Some roles are missing in database");
        }
        entity.setRoles(roleEntities);

        UserEntity saved = userJpaRepository.save(entity);
        return toDomain(saved);
    }

    private UserAccount toDomain(UserEntity userEntity) {
        Set<RoleName> roles = userEntity.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        Set<PermissionName> permissions = userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permissionEntity -> permissionEntity.getName())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        return new UserAccount(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPasswordHash(),
                userEntity.isActive(),
                roles,
                permissions,
                userEntity.getCreatedAt()
        );
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
