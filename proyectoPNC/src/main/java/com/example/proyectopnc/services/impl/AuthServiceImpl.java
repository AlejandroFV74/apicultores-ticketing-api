package com.example.proyectopnc.services.impl;
import com.example.proyectopnc.config.JwtUtil;
import com.example.proyectopnc.dto.request.LoginRequest;
import com.example.proyectopnc.dto.request.RegisterRequest;
import com.example.proyectopnc.dto.response.AuthResponse;
import com.example.proyectopnc.model.User;
import com.example.proyectopnc.enums.Role;
import com.example.proyectopnc.exception.BadRequestException;
import com.example.proyectopnc.repositories.UserRepository;
import com.example.proyectopnc.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        Role role = request.getRole() != null ? request.getRole() : Role.BUYER;

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado: {} con rol: {}", savedUser.getEmail(), savedUser.getRole());
        String token = jwtUtil.generateToken(savedUser.getUserId(), savedUser.getEmail(), savedUser.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getUserId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .message("Usuario registrado exitosamente")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            userService.recordSuccessfulLogin(request.getEmail());

            User user = (User) authentication.getPrincipal();

            String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());

            log.info("Login exitoso para: {}", user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .message("Login exitoso")
                    .build();

        } catch (BadCredentialsException e) {
            userService.recordFailedLoginAttempt(request.getEmail());
            log.warn("Intento de login fallido para: {}", request.getEmail());
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }
}