package com.auth.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "permission")
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Column(name = "description", length = 255)
    private String description;
}
