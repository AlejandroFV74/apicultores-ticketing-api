package com.apicultores.backendapicultores.domain.entity;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    private String title;
    private String description;
    private String venue;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "max_tickets_per_user")
    private Integer maxTicketsPerUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
