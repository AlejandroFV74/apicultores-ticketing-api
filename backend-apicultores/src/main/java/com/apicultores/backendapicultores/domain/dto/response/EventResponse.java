package com.apicultores.backendapicultores.domain.dto.response;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventResponse {
    private UUID eventId;
    private UUID organizerId;
    private String title;
    private String description;
    private String venue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventStatus status;
    private Integer maxTicketsPerUser;
    private LocalDateTime createdAt;
}
