package com.company.ppm.adapters.out.persistence.entity;

import com.company.ppm.domain.model.PermissionName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_permission")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 64)
    private PermissionName name;

    public Long getId() {
        return id;
    }

    public PermissionName getName() {
        return name;
    }

    public void setName(PermissionName name) {
        this.name = name;
    }
}
