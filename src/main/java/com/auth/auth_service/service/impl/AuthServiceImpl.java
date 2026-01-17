package com.auth.auth_service.service.impl;


import com.auth.auth_service.dto.request.CreateUserRequest;
import com.auth.auth_service.dto.request.UpdateUserRequest;
import com.auth.auth_service.entity.AuthUser;
import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.entity.RolePermission;
import com.auth.auth_service.entity.UserRole;
import com.auth.auth_service.enums.UserType;
import com.auth.auth_service.exception.UnauthorizedException;
import com.auth.auth_service.repository.AuthUserRepository;
import com.auth.auth_service.repository.PermissionRepository;
import com.auth.auth_service.repository.RolePermissionRepository;
import com.auth.auth_service.repository.UserRoleRepository;
import com.auth.auth_service.security.JwtTokenProvider;
import com.auth.auth_service.service.AuthService;
import com.auth.auth_service.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PermissionService permissionService;

    @Override
    public String login(String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic"))
        {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String base64Credentials = authorizationHeader.substring(6);
        String credntials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

        String[] values = credntials.split(":",2);

        if(values.length != 2)
        {
            throw new UnauthorizedException("Invalid Basic Authentication token");
        }

        String username = values[0];
        String password = values[1];

        AuthUser user = authUserRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(()->new RuntimeException("User Not Found"));

        if(!user.getIsActive())
        {
            throw new UnauthorizedException("User is not active");
        }

        if(!passwordEncoder.matches(password,user.getPassword()))
        {
            throw new UnauthorizedException("Invalid Password");
        }

        Set<String> permission = resolvePermission(user.getId());

        return jwtTokenProvider.generateToken(user.getId(), user.getUserType().name() , permission);
        }

    private Set<String> resolvePermission(Long id)
    {
        Set<String> permissions = new HashSet<>();

        List<UserRole> userRoles = userRoleRepository.findByUserIdAndIsDeletedFalse(id);

        for (UserRole userRole : userRoles)
        {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdAndIsDeletedFalse(userRole.getRoleId());

            for(RolePermission rolePermission : rolePermissions)
            {
                Permission permission = permissionRepository.findById(rolePermission.getPermissionId()).orElseThrow(()-> new RuntimeException("Permission Not Found"));
                permissions.add(permission.getCode());
            }
        }
        return permissions;
    }

    @Override
    @Transactional
    public Long createUser(CreateUserRequest request, Long requestOwnerId) {

        // 🔐 Resolve request owner
        AuthUser requestOwner = authUserRepository.findById(requestOwnerId)
                .orElseThrow(() -> new RuntimeException("Request owner not found"));

        // 🔐 Permission check: can create users?
        if (!permissionService.hasPermission(requestOwnerId, "USER_CREATE")) {
            throw new RuntimeException("You do not have permission to create users");
        }

        // 🔐 SUPER_ADMIN escalation protection
        if (request.getUserType() == UserType.SUPER_ADMIN &&
                requestOwner.getUserType() != UserType.SUPER_ADMIN) {
            throw new RuntimeException(
                    "Only SUPER_ADMIN can create another SUPER_ADMIN"
            );
        }

        // 🔐 Username uniqueness
        if (authUserRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // ✅ Create user
        AuthUser user = new AuthUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(request.getUserType());
        user.setCreatedByAdminId(requestOwnerId);
        user.setIsActive(true);

        authUserRepository.save(user);
        return user.getId();
    }

    // ===================== UPDATE USER =====================
    @Override
    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request, Long requestOwnerId) {

        // 🔐 Permission check
        if (!permissionService.hasPermission(requestOwnerId, "USER_UPDATE")) {
            throw new RuntimeException("You do not have permission to update users");
        }

        AuthUser user = authUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(request.getIsActive());
        authUserRepository.save(user);
    }

    // ===================== DELETE USER (SOFT DELETE) =====================
    @Override
    @Transactional
    public void deleteUser(Long userId, Long requestOwnerId) {

        // 🔐 Permission check
        if (!permissionService.hasPermission(requestOwnerId, "USER_DELETE")) {
            throw new RuntimeException("You do not have permission to delete users");
        }

        AuthUser user = authUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔐 Protect SUPER_ADMIN from deletion by non-SUPER_ADMIN
        if (user.getUserType() == UserType.SUPER_ADMIN) {

            AuthUser requestOwner = authUserRepository.findById(requestOwnerId)
                    .orElseThrow(() -> new RuntimeException("Request owner not found"));

            if (requestOwner.getUserType() != UserType.SUPER_ADMIN) {
                throw new RuntimeException(
                        "Only SUPER_ADMIN can delete another SUPER_ADMIN"
                );
            }
        }

        // ✅ Soft delete
        user.setIsActive(false);
        authUserRepository.save(user);
    }
}
