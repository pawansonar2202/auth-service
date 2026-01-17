package com.auth.auth_service.service;

import com.auth.auth_service.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(String name, String description);

    List<Role> getAllRoles();

    Role updateRoleDescription(Long roleId, String description);

    void disableRole(Long roleId);

}
