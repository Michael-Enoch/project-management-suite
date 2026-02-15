package com.company.ppm.adapters.out.persistence;

import com.company.ppm.adapters.out.persistence.entity.RoleEntity;
import com.company.ppm.adapters.out.persistence.entity.UserEntity;
import com.company.ppm.adapters.out.persistence.repository.RoleJpaRepository;
import com.company.ppm.adapters.out.persistence.repository.UserJpaRepository;
import com.company.ppm.domain.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountPersistenceAdapterTest {

    @Test
    void saveClearsExistingRolesWhenRequestedRolesAreEmpty() {
        UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);
        RoleJpaRepository roleJpaRepository = mock(RoleJpaRepository.class);
        UserAccountPersistenceAdapter adapter = new UserAccountPersistenceAdapter(userJpaRepository, roleJpaRepository);

        UUID userId = UUID.randomUUID();
        UserEntity existing = new UserEntity();
        existing.setId(userId);
        existing.setEmail("existing@example.com");
        existing.setPasswordHash("hash");
        existing.setActive(true);
        existing.setCreatedAt(Instant.now());

        RoleEntity existingRole = new RoleEntity();
        existingRole.setName(com.company.ppm.domain.model.RoleName.ADMIN);
        existing.setRoles(new HashSet<>(Set.of(existingRole)));

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userJpaRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount saved = adapter.save(new UserAccount(
                userId,
                "existing@example.com",
                "hash",
                true,
                Set.of(),
                Set.of(),
                Instant.now()
        ));

        assertThat(saved.roles()).isEmpty();
        verify(roleJpaRepository, never()).findByNameIn(any());
    }

    @Test
    void findByEmailNormalizesInput() {
        UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);
        RoleJpaRepository roleJpaRepository = mock(RoleJpaRepository.class);
        UserAccountPersistenceAdapter adapter = new UserAccountPersistenceAdapter(userJpaRepository, roleJpaRepository);

        when(userJpaRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.empty());

        adapter.findByEmail("  USER@EXAMPLE.COM ");

        verify(userJpaRepository).findByEmailIgnoreCase("user@example.com");
    }
}
