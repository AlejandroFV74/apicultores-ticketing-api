package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.domain.dto.request.UpdateRoleRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateUserRequest;
import com.apicultores.backendapicultores.domain.dto.response.UserResponse;
import com.apicultores.backendapicultores.common.enums.Role;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Value("${security.brute-force.lockout-duration:30}")
    private int lockoutDuration;

    @Value("${security.brute-force.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + normalizedEmail));

        boolean wasLocked = !user.isAccountNonLocked();
        if (user.isAccountLocked(lockoutDuration)) {
            log.warn("Intento de login en cuenta bloqueada: {}", normalizedEmail);
            throw new LockedException("Cuenta bloqueada. Intente más tarde.");
        }

        if (wasLocked) {
            userRepository.save(user);
        }

        return user;
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + normalizedEmail));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(UUID userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);

        log.info("Rol de usuario actualizado: {} -> {}", user.getEmail(), request.getRole());

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("Usuario eliminado: {}", userId);
    }

    @Override
    @Transactional
    public UserResponse disableUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        user.setEnabled(false);
        User updatedUser = userRepository.save(user);

        log.info("Usuario deshabilitado: {}", user.getEmail());
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse enableUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        user.setEnabled(true);
        user.resetFailedAttempts();
        User updatedUser = userRepository.save(user);

        log.info("Usuario habilitado: {}", user.getEmail());
        return mapToResponse(updatedUser);
    }

    @Transactional
    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        String email = normalizeEmail(request.getEmail());
        if (!email.isEmpty()) {
            if (!user.getEmail().equals(email) &&
                    userRepository.existsByEmail(email)) {
                throw new BadRequestException("El email ya está en uso por otro usuario");
            }
            user.setEmail(email);
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado: {}", updatedUser.getEmail());

        return mapToResponse(updatedUser);
    }


    @Override
    @Transactional
    public void recordFailedLoginAttempt(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user != null) {
            user.incrementFailedAttempts();
            if (user.getFailedAttempts() >= maxFailedAttempts) {
                user.lockAccount();
                log.warn("Cuenta bloqueada por múltiples intentos fallidos: {}", normalizedEmail);
            }

            userRepository.save(user);
            log.debug("Intento fallido registrado para: {}. Intentos: {}", normalizedEmail, user.getFailedAttempts());
        }
    }

    @Override
    @Transactional
    public void recordSuccessfulLogin(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user != null) {
            user.resetFailedAttempts();
            user.updateLastLogin();
            userRepository.save(user);
            log.debug("Login exitoso registrado para: {}", normalizedEmail);
        }
    }

    public boolean isAccountLocked(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user != null) {
            return user.isAccountLocked(lockoutDuration);
        }
        return false;
    }

    @Transactional
    @Override
    public void unlockAccount(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + normalizedEmail));
        user.resetFailedAttempts();
        userRepository.save(user);
        log.info("Cuenta desbloqueada manualmente: {}", normalizedEmail);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}