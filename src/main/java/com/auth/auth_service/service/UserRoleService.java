package com.auth.auth_service.service;

public interface UserRoleService {
    void assignRoleToUser(Long userId, Long roleId);
}
