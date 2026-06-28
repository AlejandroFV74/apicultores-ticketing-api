package com.apicultores.backendapicultores.services.notification;

import com.apicultores.backendapicultores.common.enums.NotificationType;
import com.apicultores.backendapicultores.common.mappers.NotificationMapper;
import com.apicultores.backendapicultores.domain.dto.response.notification.NotificationResponse;
import com.apicultores.backendapicultores.domain.entity.Notification;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private final Map<UUID, List<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(0L); // sin timeout; el cliente reconecta si se cae
        emittersByUser.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }
        return emitter;
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> list = emittersByUser.get(userId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        emittersByUser.forEach((userId, emitters) ->
                emitters.forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                    } catch (IOException e) {
                        removeEmitter(userId, emitter);
                    }
                })
        );
    }

    public Notification createAndPush(User user, NotificationType type, String title, String message,
                                      UUID eventId, UUID reservationId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .eventId(eventId)
                .reservationId(reservationId)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        pushLive(user.getUserId(), notificationMapper.toDto(saved));
        return saved;
    }

    private void pushLive(UUID userId, NotificationResponse payload) {
        List<SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            log.debug("Usuario {} no tiene conexión SSE activa, la notificación quedó persistida", userId);
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (IOException e) {
                removeEmitter(userId, emitter);
            }
        }
    }

    public List<NotificationResponse> getMyNotifications(UUID userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public long countUnread(UUID userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notificación no encontrada");
        }

        notification.setIsRead(true);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }
}