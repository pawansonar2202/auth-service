package com.auth.auth_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class JwtContext {

    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Long getUserId() {
        if (auth().getPrincipal() instanceof JwtAuthenticationFilter.JwtPrincipal p) {
            return p.userId();
        }
        return null;
    }

    public String getUserType() {
        if (auth().getPrincipal() instanceof JwtAuthenticationFilter.JwtPrincipal p) {
            return p.userType();
        }
        return null;
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(getUserType());
    }

    public boolean hasPermission(String permission) {
        return auth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }
}

