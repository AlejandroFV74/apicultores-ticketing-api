package com.apicultores.backendapicultores.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatsResponse {
    private UUID eventId;
    private String title;

    private long seatsTotal;
    private long seatsAvailable;
    private long seatsReserved;
    private long seatsSold;
    private double occupancyRate;

    private BigDecimal revenue;
    private long waitlistCount;
    private List<SeatTypeStat> byType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatTypeStat {
        private String seatType;
        private long total;
        private long available;
        private long reserved;
        private long sold;
        private BigDecimal revenue;
    }
}