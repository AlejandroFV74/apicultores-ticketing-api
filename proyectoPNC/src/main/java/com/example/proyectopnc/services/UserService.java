package com.example.proyectopnc.services;

import com.example.proyectopnc.dto.request.UpdateRoleRequest;
import com.example.proyectopnc.dto.request.UpdateUserRequest;
import com.example.proyectopnc.dto.response.UserResponse;
import com.example.proyectopnc.enums.Role;
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