package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.domain.dto.response.notification.NotificationResponse;
import com.apicultores.backendapicultores.domain.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationResponse toDto(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .eventId(n.getEventId())
                .reservationId(n.getReservationId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}