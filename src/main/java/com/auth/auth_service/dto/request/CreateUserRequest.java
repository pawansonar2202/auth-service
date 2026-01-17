package com.auth.auth_service.dto.request;

import com.auth.auth_service.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 100, message = "Username must be between 4 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "User type is required")
    private UserType userType;

    // Nullable only for SUPER_ADMIN
    private Long createdByAdminId;
}

