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
@Table(name = "ticket_transfer_history")
public class TicketTransferHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_transfer_id", updatable = false, nullable = false)
    private UUID ticketTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "from_user_id", nullable = false)
    private UUID fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private UUID toUserId;

    @Column(name = "changed_by_user_id", nullable = false)
    private UUID changedByUserId;

    @Column(name = "transferred_at", nullable = false, updatable = false)
    private LocalDateTime transferredAt;

    @PrePersist
    protected void onCreate() {
        this.transferredAt = LocalDateTime.now();
    }
}
