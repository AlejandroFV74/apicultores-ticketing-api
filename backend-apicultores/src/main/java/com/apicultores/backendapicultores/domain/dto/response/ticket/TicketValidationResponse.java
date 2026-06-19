package com.apicultores.backendapicultores.domain.dto.response.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponse {
    String confirmation;
}
