package com.auth.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "role_permission")
public class RolePermission extends BaseEntity{

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    /**
     * Who granted this permission to the role
     * (SUPER_ADMIN / ADMIN id)
     */
    @Column(name = "granted_by", nullable = false)
    private Long grantedBy;

    /**
     * When permission was granted
     */
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();

    /**
     * Whether this permission is currently active for the role
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
