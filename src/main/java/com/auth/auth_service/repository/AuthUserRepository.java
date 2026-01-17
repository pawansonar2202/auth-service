package com.auth.auth_service.repository;

import com.auth.auth_service.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser,Long> {

    Optional<AuthUser> findByUsernameAndIsDeletedFalse(String username);

    Optional<AuthUser> findByIdAndIsDeletedFalse(Long id);

    List<AuthUser> findByCreatedByAdminIdAndIsDeletedFalse(Long adminId);

    boolean existsByIdAndCreatedByAdminIdAndIsDeletedFalse(Long userId, Long adminId);
}
