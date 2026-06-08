package com.apicultores.backendapicultores.domain.entities;

import com.apicultores.backendapicultores.common.SeatStatus;
import com.apicultores.backendapicultores.common.SeatType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_event_seat_number", columnNames = {"event_id", "seat_number"})
        }
)

public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id", updatable = false, nullable = false)
    private UUID seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToMany(mappedBy = "seats")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();
}
