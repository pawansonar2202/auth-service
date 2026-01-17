package com.auth.auth_service.controller;

import com.auth.auth_service.dto.request.AssignRoleRequest;
import com.auth.auth_service.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> assignRole(
            @Valid @RequestBody AssignRoleRequest request
    ) {
        userRoleService.assignRoleToUser(
                request.getUserId(),
                request.getRoleId()
        );
        return ResponseEntity.ok("Role assigned successfully");
    }
}


