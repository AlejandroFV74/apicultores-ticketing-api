package com.apicultores.backendapicultores.domain.dto.response;
import com.apicultores.backendapicultores.common.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private UUID id;
    private TicketStatus status;
    private String message;
}
