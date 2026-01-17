package com.auth.auth_service.controller;

import com.auth.auth_service.dto.request.CreatePermissionRequest;
import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Create a new permission
     * Only SUPER_ADMIN should be allowed (checked in service)
     */
    @PostMapping
    public Permission createPermission(
            @RequestBody @Valid CreatePermissionRequest request
    ) {

        return permissionService.createPermission(
                request.getCode(),
                request.getDescription()
        );
    }

    /**
     * Get all permissions (non-deleted)
     */
    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
}
