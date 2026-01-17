package com.auth.auth_service.controller;

import com.auth.auth_service.dto.request.CreateUserRequest;
import com.auth.auth_service.dto.request.UpdateUserRequest;
import com.auth.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String jwtToken = authService.login(auth);
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    // ===================== CREATE USER =====================
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping("/users")
    public ResponseEntity<Map<String, Long>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        Long requestOwnerId = getCurrentUserId();
        Long userId = authService.createUser(request, requestOwnerId);

        return ResponseEntity.ok(Map.of("userId", userId));
    }


    // ===================== UPDATE USER =====================
    @PreAuthorize("hasAuthority('UPDATE_CREATE')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        Long requestOwnerId = getCurrentUserId();
        authService.updateUser(userId, request, requestOwnerId);
        return ResponseEntity.ok("User updated successfully");
    }

    // ===================== DELETE USER (SOFT DELETE) =====================
    @PreAuthorize("hasAuthority('DELETE_CREATE')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId) {

        Long requestOwnerId = getCurrentUserId();
        authService.deleteUser(userId, requestOwnerId);
        return ResponseEntity.ok("User deleted successfully");
    }

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        return Long.valueOf(authentication.getPrincipal().toString());
    }

}
