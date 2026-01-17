package com.auth.auth_service.service.impl;

import com.auth.auth_service.entity.RolePermission;
import com.auth.auth_service.repository.RolePermissionRepository;
import com.auth.auth_service.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {

        // Prevent duplicate mapping
        if (rolePermissionRepository
                .existsByRoleIdAndPermissionIdAndIsDeletedFalse(roleId, permissionId)) {
            throw new IllegalStateException("Permission already assigned to this role");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);

        rolePermissionRepository.save(rolePermission);
    }
}

