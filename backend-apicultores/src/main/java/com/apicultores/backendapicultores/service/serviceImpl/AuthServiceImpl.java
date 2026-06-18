package com.apicultores.backendapicultores.service.serviceImpl;
import com.apicultores.backendapicultores.config.security.JwtUtil;
import com.apicultores.backendapicultores.domain.dto.request.LoginRequest;
import com.apicultores.backendapicultores.domain.dto.request.RegisterRequest;
import com.apicultores.backendapicultores.domain.dto.response.AuthResponse;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.common.enums.Role;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new BadRequestException("El nombre completo es requerido");
        }

        String email = normalizeEmail(request.getEmail());
        if (email.isEmpty()) {
            throw new BadRequestException("El email es requerido");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("El email ya está registrado");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.BUYER;

        if (role == Role.ADMIN) {
            throw new BadRequestException("No se puede registrar como ADMIN");
        }
        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado: {} con rol: {}", savedUser.getEmail(), savedUser.getRole());

        // Generar token
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
        String email = normalizeEmail(request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            userService.recordSuccessfulLogin(email);

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
            userService.recordFailedLoginAttempt(email);
            log.warn("Intento de login fallido para: {}", email);
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}