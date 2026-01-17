package com.auth.auth_service.controller;

import com.auth.auth_service.dto.request.AssignPermissionToRoleRequest;
import com.auth.auth_service.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> assignPermissionToRole(
            @Valid @RequestBody AssignPermissionToRoleRequest request
    ) {
        rolePermissionService.assignPermissionToRole(
                request.getRoleId(),
                request.getPermissionId()
        );
        return ResponseEntity.ok("Permission assigned to role successfully");
    }
}
