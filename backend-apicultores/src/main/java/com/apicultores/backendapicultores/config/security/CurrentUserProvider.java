package com.apicultores.backendapicultores.config.security;

import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    public UUID getCurrentUserId() {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No token provided");
        }

        String token = authHeader.substring(7);

        String userIdString = jwtUtil.extractUserId(token);

        if (userIdString == null) {
            throw new BadRequestException("Invalid token: no userId");
        }

        return UUID.fromString(userIdString);
    }
}
