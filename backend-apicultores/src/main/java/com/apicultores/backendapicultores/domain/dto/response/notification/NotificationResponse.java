package com.apicultores.backendapicultores.domain.dto.response.notification;

import com.apicultores.backendapicultores.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private UUID notificationId;
    private NotificationType type;
    private String title;
    private String message;
    private UUID eventId;
    private UUID reservationId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}