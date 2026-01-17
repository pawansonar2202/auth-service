package com.auth.auth_service.controller;

import com.auth.auth_service.dto.request.CreateRoleRequest;
import com.auth.auth_service.dto.request.UpdateRoleRequest;
import com.auth.auth_service.entity.Role;
import com.auth.auth_service.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public Role createRole(
            @RequestBody @Valid CreateRoleRequest request
    ) {
        return roleService.createRole(
                request.getName(),
                request.getDescription()
        );
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PutMapping("/{id}")
    public Role updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRoleRequest request
    ) {
        return roleService.updateRoleDescription(
                id,
                request.getDescription()
        );
    }

    @DeleteMapping("/{id}")
    public void disableRole(@PathVariable Long id) {
        roleService.disableRole(id);
    }
}
