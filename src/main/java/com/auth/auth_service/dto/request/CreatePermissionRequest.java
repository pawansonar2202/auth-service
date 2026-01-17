package com.auth.auth_service.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequest {

    @NotBlank
    private String code;

    private String description;
}
