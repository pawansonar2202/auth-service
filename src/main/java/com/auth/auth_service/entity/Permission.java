package com.auth.auth_service.entity;

import com.auth.auth_service.enums.PermissionCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private PermissionCode code;

    @Column(name = "description", length = 255)
    private String description;
}
