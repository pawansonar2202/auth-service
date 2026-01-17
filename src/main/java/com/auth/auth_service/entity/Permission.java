package com.auth.auth_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@Table(name = "permission")
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Column(name = "description", length = 255)
    private String description;
}
