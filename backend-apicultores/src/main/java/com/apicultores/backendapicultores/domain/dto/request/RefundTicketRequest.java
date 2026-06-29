package com.apicultores.backendapicultores.domain.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RefundTicketRequest {
    private UUID ticketId;
    private BigDecimal amount;
    private String reason;
}
