package com.apicultores.backendapicultores.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferTicketRequest {
    private UUID ticketId;
    private UUID toUserId;
}
