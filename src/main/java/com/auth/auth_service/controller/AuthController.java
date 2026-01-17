package com.auth.auth_service.controller;

import com.auth.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login (HttpServletRequest httpServletRequest)
    {
        String auth = httpServletRequest.getHeader("Authorization");

        String jwtToken = authService.login(auth);

        return ResponseEntity.ok(Map.of("token",jwtToken));

    }
}
