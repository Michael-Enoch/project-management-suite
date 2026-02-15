package com.company.ppm.adapters.out.persistence.repository;

import com.company.ppm.adapters.out.persistence.entity.RoleEntity;
import com.company.ppm.domain.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    List<RoleEntity> findByNameIn(Collection<RoleName> names);
}
