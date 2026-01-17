package com.auth.auth_service.service;

public interface RolePermissionService {
    void assignPermissionToRole(Long roleId, Long permissionId);
}
