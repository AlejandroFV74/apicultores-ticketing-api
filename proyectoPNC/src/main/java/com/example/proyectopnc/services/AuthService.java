package com.example.proyectopnc.services;

import com.example.proyectopnc.dto.request.LoginRequest;
import com.example.proyectopnc.dto.request.RegisterRequest;
import com.example.proyectopnc.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}