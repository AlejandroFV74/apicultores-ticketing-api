package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.LoginRequest;
import com.apicultores.backendapicultores.domain.dto.request.RegisterRequest;
import com.apicultores.backendapicultores.domain.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}