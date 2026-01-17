package com.auth.auth_service.repository;

import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.entity.Role;
import com.auth.auth_service.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission,Long> {

    // Fetch permissions assigned to a role
    List<RolePermission> findByRoleIdAndIsDeletedFalse(Long roleId);

    // Prevent duplicate role-permission mapping
    boolean existsByRoleIdAndPermissionIdAndIsDeletedFalse(
            Long roleId,
            Long permissionId
    );

    // Fetch specific mapping
    Optional<RolePermission> findByRoleIdAndPermissionIdAndIsDeletedFalse(
            Long roleId,
            Long permissionId
    );

    boolean existsByRoleIdInAndPermissionId(
            List<Long> roleIds,
            Long permissionId
    );
}
