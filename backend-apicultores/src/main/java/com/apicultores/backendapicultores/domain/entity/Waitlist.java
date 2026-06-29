package com.apicultores.backendapicultores.domain.entity;

import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.common.enums.WaitlistStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "waitlist",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_register_user", columnNames = {"user_id", "event_id", "seat_type"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "waitlist_id")
    private UUID waitlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WaitlistStatus status = WaitlistStatus.WAITING;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    // Reserva temporal (15 min)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}