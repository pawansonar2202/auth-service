package com.auth.auth_service.service.impl;


import com.auth.auth_service.entity.AuthUser;
import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.entity.RolePermission;
import com.auth.auth_service.entity.UserRole;
import com.auth.auth_service.exception.UnauthorizedException;
import com.auth.auth_service.repository.AuthUserRepository;
import com.auth.auth_service.repository.PermissionRepository;
import com.auth.auth_service.repository.RolePermissionRepository;
import com.auth.auth_service.repository.UserRoleRepository;
import com.auth.auth_service.security.JwtTokenProvider;
import com.auth.auth_service.service.AuthService;
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
                permissions.add(permission.getCode().name());
            }
        }
        return permissions;
    }
}
