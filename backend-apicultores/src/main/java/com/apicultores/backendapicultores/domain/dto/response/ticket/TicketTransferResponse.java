package com.apicultores.backendapicultores.domain.dto.response.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTransferResponse {
    private UUID ticketId;
    private UUID fromUserId;
    private UUID toUserId;
    private LocalDateTime transferredAt;
}
