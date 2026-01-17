package com.auth.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;

    private Long createdByAdminId;
}