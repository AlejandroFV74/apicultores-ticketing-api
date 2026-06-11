package com.apicultores.backendapicultores.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

//just for the usage in seat and try
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", updatable = false, nullable = false)
    private UUID eventId;

    private String title;

    private LocalDateTime startDate;
}
