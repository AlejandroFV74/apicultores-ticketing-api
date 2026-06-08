package com.apicultores.backendapicultores.domain.entities;

import com.apicultores.backendapicultores.domain.entities.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_id", updatable = false, nullable = false)
    private UUID ticket_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="seat_id", nullable = false)
    private Seat seat;
    @ManyToOne(fetch  = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UUID owner_id;

    @Column(name = "qr_code", nullable = false, unique = true, length = 255)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

