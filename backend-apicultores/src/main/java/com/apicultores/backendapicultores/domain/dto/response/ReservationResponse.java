package com.apicultores.backendapicultores.domain.dto.response;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private UUID id;
    private List<SeatResponse> seats;
    private ReservationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private UUID eventId;
}
