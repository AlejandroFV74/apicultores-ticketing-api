package com.apicultores.backendapicultores.controller;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return notificationService.subscribe(currentUserProvider.getCurrentUserId());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> myNotifications() {
        return buildResponse("Notificaciones", HttpStatus.OK,
                notificationService.getMyNotifications(currentUserProvider.getCurrentUserId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<GeneralResponse> unreadCount() {
        return buildResponse("Notificaciones sin leer", HttpStatus.OK,
                notificationService.countUnread(currentUserProvider.getCurrentUserId()));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<GeneralResponse> markAsRead(@PathVariable UUID notificationId) {
        return buildResponse("Notificación marcada como leída", HttpStatus.OK,
                notificationService.markAsRead(notificationId, currentUserProvider.getCurrentUserId()));
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity.status(status).body(GeneralResponse.builder()
                .uri(uri).message(message).status(status.value()).time(LocalDateTime.now()).data(data).build());
    }
}