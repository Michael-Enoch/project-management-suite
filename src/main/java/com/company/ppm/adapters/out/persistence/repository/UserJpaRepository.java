package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserEntity> findById(UUID id);
}
