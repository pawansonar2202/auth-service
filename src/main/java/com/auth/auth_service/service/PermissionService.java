package com.auth.auth_service.service;

import com.auth.auth_service.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(String code, String description);

    List<Permission> getAllPermissions();

    boolean hasPermission(Long userId, String permissionCode);

}
