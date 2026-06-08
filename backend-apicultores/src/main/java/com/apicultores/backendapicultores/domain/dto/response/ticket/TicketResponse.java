package com.apicultores.backendapicultores.domain.dto.response.ticket;

import com.apicultores.backendapicultores.common.SeatType;
import com.apicultores.backendapicultores.common.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private UUID ticketId;
    private String qrCode;
    private TicketStatus status;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
    private Integer seatNumber;
    private SeatType seatType;
    private String eventName;
    private LocalDateTime eventDate;
    private String ownerName;
}