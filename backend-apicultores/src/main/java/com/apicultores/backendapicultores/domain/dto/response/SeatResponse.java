package com.apicultores.backendapicultores.domain.dto.response;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
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
public class SeatResponse {
    private UUID id;
    private String seatNumber;
    private SeatType seatType;
    private Double price;
    private SeatStatus status;
    private LocalDateTime createdAt;
}
