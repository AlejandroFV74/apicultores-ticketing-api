package com.example.proyectopnc.config;

import com.example.proyectopnc.enums.Role;
import com.example.proyectopnc.model.User;
import com.example.proyectopnc.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.enabled:true}")
    private boolean adminEnabled;

    @Value("${admin.default.email:admin@ticketing.com}")
    private String adminEmail;

    @Value("${admin.default.password:Admin123!@#}")
    private String adminPassword;

    @Value("${admin.default.full-name:System Administrator}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        if (!adminEnabled) {
            log.info("Creación de ADMIN deshabilitada por configuración");
            return;
        }
        boolean adminExists = userRepository.findByRole(Role.ADMIN).stream().findAny().isPresent();

        if (!adminExists) {
            User admin = User.builder()
                    .fullName(adminFullName)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .accountNonLocked(true)
                    .failedAttempts(0)
                    .build();

            userRepository.save(admin);
            log.info("========================================");
            log.info("Usuario ADMIN creado automáticamente");
            log.info("   Email: {}", adminEmail);
            log.info("   Contraseña: {}", adminPassword);
            log.info("========================================");
        } else {
            log.debug("Ya existe un usuario ADMIN en el sistema");
        }
    }
}