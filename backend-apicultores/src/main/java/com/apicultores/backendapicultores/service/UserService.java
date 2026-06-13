package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.UpdateRoleRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateUserRequest;
import com.apicultores.backendapicultores.domain.dto.response.UserResponse;
import com.apicultores.backendapicultores.common.enums.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID userId);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    UserResponse updateUserRole(UUID userId, UpdateRoleRequest request);
    void deleteUser(UUID userId);
    void recordFailedLoginAttempt(String email);
    void recordSuccessfulLogin(String email);
    UserResponse disableUser(UUID userId);
    UserResponse enableUser(UUID userId);
    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    @Transactional
    void unlockAccount(String email);
}