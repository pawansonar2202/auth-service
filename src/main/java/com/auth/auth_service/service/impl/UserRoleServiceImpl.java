package com.auth.auth_service.service.impl;

import com.auth.auth_service.entity.UserRole;
import com.auth.auth_service.repository.UserRoleRepository;
import com.auth.auth_service.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {

        // Prevent duplicate mapping
        if (userRoleRepository.existsByUserIdAndRoleIdAndIsDeletedFalse(userId, roleId)) {
            throw new IllegalStateException("User already has this role");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);

        userRoleRepository.save(userRole);
    }
}
