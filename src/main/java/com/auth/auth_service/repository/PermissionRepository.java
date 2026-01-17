package com.auth.auth_service.repository;

import com.auth.auth_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

    boolean existsByCodeAndIsDeletedFalse(String code);

    Optional<Permission> findByCodeAndIsDeletedFalse(String code);

    List<Permission> findAllByIsDeletedFalse();
}
