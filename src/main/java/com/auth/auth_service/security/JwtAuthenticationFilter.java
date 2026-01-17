package com.auth.auth_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("---- JWT FILTER START ----");
        System.out.println("Request URI: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token found, skipping JWT processing");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Token extracted");

        if (!jwtTokenProvider.validateToken(token)) {
            System.out.println("Token validation FAILED");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Token validation SUCCESS");

        Long userId = jwtTokenProvider.getUserId(token);
        String userType = jwtTokenProvider.getUserType(token);
        Set<String> permissions = jwtTokenProvider.getPermissions(token);

        System.out.println("UserId from JWT: " + userId);
        System.out.println("UserType from JWT: " + userType);
        System.out.println("Permissions from JWT: " + permissions);

        List<SimpleGrantedAuthority> authorities =
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        System.out.println("Authorities created: " + authorities);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        permissions,
                        authorities
                );

        authentication.setDetails(userType);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Authentication set in SecurityContext");
        System.out.println("---- JWT FILTER END ----");

        filterChain.doFilter(request, response);
    }
}
