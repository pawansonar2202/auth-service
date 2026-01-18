package com.auth.auth_service.service.impl;

import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.repository.PermissionRepository;
import com.auth.auth_service.repository.RolePermissionRepository;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRoleRepository;
import com.auth.auth_service.security.JwtContext;
import com.auth.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final JwtContext jwtContext;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public Permission createPermission(String code, String description) {

        System.out.println("Super admin ->" + jwtContext.isSuperAdmin());
        if (!jwtContext.isSuperAdmin()) {
            throw new RuntimeException("Only SUPER_ADMIN can create permissions");
        }

        String normalizedCode = code.trim().toUpperCase();

        if (permissionRepository.existsByCodeAndIsDeletedFalse(normalizedCode)) {
            throw new RuntimeException(
                    "Permission already exists: " + normalizedCode
            );
        }

        Permission permission = Permission.builder()
                .code(normalizedCode)
                .description(description)
                .build();

        return permissionRepository.save(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllByIsDeletedFalse();
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {

        // 1️⃣ Get role IDs of user
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return false;
        }

        // 2️⃣ Get permission ID by code
        Permission permission = permissionRepository.findByCode(permissionCode)
                .orElse(null);
        if (permission.getId() == null) {
            return false;
        }

        // 3️⃣ Check role-permission mapping (FIXED)
        return rolePermissionRepository
                .existsByRoleIdInAndPermissionId(roleIds, permission.getId());
    }
}
