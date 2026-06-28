package com.apicultores.backendapicultores.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservation_status_history")
public class ReservationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_status_hist_id", updatable = false, nullable = false)
    private UUID reservationStatusHistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "changed_by_user_id")
    private UUID changedByUserId;

    @Column(name = "from_status", length = 20)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 20)
    private String toStatus;

    @Column(name = "change_at", nullable = false, updatable = false)
    private LocalDateTime changeAt;

    @PrePersist
    protected void onCreate() {
        this.changeAt = LocalDateTime.now();
    }

}