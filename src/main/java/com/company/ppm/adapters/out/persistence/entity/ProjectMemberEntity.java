package com.company.ppm.adapters.out.persistence.entity;

import com.company.ppm.domain.model.MembershipRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "project_member")
public class ProjectMemberEntity {

    @EmbeddedId
    private ProjectMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_role", nullable = false, length = 32)
    private MembershipRole membershipRole;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ProjectMemberId getId() {
        return id;
    }

    public void setId(ProjectMemberId id) {
        this.id = id;
    }

    public MembershipRole getMembershipRole() {
        return membershipRole;
    }

    public void setMembershipRole(MembershipRole membershipRole) {
        this.membershipRole = membershipRole;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
