package com.apicultores.backendapicultores.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalEvents;
    private long totalTickets;
    private long totalReservations;
    private long pendingReservations;
    private long activeEvents;
    private long upcomingEvents;
}