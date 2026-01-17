package com.auth.auth_service.service.impl;

import com.auth.auth_service.entity.Role;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRoleRepository;
import com.auth.auth_service.security.JwtContext;
import com.auth.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtContext jwtContext;

    @Override
    public Role createRole(String name, String description) {

        if (!jwtContext.isSuperAdmin()) {
            throw new RuntimeException("Only SUPER_ADMIN can create roles");
        }

        String normalizedName = name.trim().toUpperCase();

        if (roleRepository.existsByNameAndIsDeletedFalse(normalizedName)) {
            throw new RuntimeException(
                    "Role already exists: " + normalizedName
            );
        }

        Role role = Role.builder()
                .name(normalizedName)
                .description(description)
                .isDeleted(false)
                .build();

        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAllByIsDeletedFalse();
    }

    @Override
    public Role updateRoleDescription(Long roleId, String description) {

        if (!jwtContext.isSuperAdmin()) {
            throw new RuntimeException("Only SUPER_ADMIN can update roles");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getIsDeleted()) {
            throw new RuntimeException("Cannot update a disabled role");
        }

        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Override
    public void disableRole(Long roleId) {

        if (!jwtContext.isSuperAdmin()) {
            throw new RuntimeException("Only SUPER_ADMIN can disable roles");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getIsDeleted()) {
            throw new RuntimeException("Role is already disabled");
        }

        // 🔐 FOREIGN KEY SAFETY CHECK
        if (userRoleRepository.existsByRoleIdAndIsDeletedFalse(roleId)) {
            throw new RuntimeException(
                    "Role is assigned to users. Remove user-role mappings first."
            );
        }

        role.setIsDeleted(true);
        roleRepository.save(role);
    }
}
