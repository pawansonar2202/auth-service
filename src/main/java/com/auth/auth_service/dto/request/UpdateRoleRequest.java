package com.auth.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {

    @NotBlank
    private String description;
}
