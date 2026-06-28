package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.JoinWaitlistRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.exception.custom.UserNotFoundException;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.services.waitlist.WaitlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/waitlist")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping
    public ResponseEntity<GeneralResponse> join(@Valid @RequestBody JoinWaitlistRequest request) {
        User user = currentUser();
        return buildResponse("Te uniste a la lista de espera", HttpStatus.CREATED,
                waitlistService.join(user, request));
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{waitlistId}")
    public ResponseEntity<GeneralResponse> leave(@PathVariable UUID waitlistId) {
        waitlistService.leave(waitlistId, currentUserProvider.getCurrentUserId());
        return buildResponse("Saliste de la lista de espera", HttpStatus.OK, null);
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> myWaitlist() {
        return buildResponse("Tu lista de espera", HttpStatus.OK,
                waitlistService.getMyWaitlist(currentUserProvider.getCurrentUserId()));
    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> eventWaitlist(@PathVariable UUID eventId) {
        return buildResponse("Lista de espera del evento", HttpStatus.OK,
                waitlistService.getEventWaitlist(eventId));
    }

    private User currentUser() {
        return userRepository.findById(currentUserProvider.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity.status(status).body(GeneralResponse.builder()
                .uri(uri).message(message).status(status.value()).time(LocalDateTime.now()).data(data).build());
    }
}