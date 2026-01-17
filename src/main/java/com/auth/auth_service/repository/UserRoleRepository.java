package com.auth.auth_service.repository;

import com.auth.auth_service.entity.Role;
import com.auth.auth_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    // Fetch all roles assigned to a user
    List<UserRole> findByUserIdAndIsDeletedFalse(Long userId);

    // Prevent duplicate role assignment
    boolean existsByUserIdAndRoleIdAndIsDeletedFalse(
            Long userId,
            Long roleId
    );

    // Fetch specific assignment
    Optional<UserRole> findByUserIdAndRoleIdAndIsDeletedFalse(
            Long userId,
            Long roleId
    );

    boolean existsByRoleIdAndIsDeletedFalse(Long roleId);

    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
}
