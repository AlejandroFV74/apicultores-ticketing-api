package com.apicultores.backendapicultores.domain.entity;


import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import java.time.LocalDateTime;
import java.util.UUID;
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
    private UUID seatId;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
  
    
    @Column(name = "seat_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Double price;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

}
