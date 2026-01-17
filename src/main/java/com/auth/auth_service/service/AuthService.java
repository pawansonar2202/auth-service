package com.auth.auth_service.service;

import com.auth.auth_service.dto.request.CreateUserRequest;
import com.auth.auth_service.dto.request.UpdateUserRequest;

public interface AuthService {
    String login(String authorizationHeader);

    Long createUser(CreateUserRequest request, Long requestOwnerId);

    void updateUser(Long userId, UpdateUserRequest request, Long requestOwnerId);

    void deleteUser(Long userId, Long requestOwnerId);
}
