package com.apicultores.backendapicultores.domain.dto.response;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EventReportResponse {
    private UUID eventId;
    private SeatReportDTO VIP;
    private SeatReportDTO GENERAL;

}