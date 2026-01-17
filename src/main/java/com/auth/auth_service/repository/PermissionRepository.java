package com.auth.auth_service.repository;

import com.auth.auth_service.entity.Permission;
import com.auth.auth_service.enums.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

    Optional<Permission> findByCodeAndIsDeletedFalse(PermissionCode code);

    List<Permission> findAllByIsDeletedFalse();

    boolean existsByCodeAndIsDeletedFalse(PermissionCode code);
}
