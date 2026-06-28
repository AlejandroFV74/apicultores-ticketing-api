package com.apicultores.backendapicultores.domain.dto.response.waitlist;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.common.enums.WaitlistStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitlistResponse {
    private UUID waitlistId;
    private UUID eventId;
    private String eventName;
    private SeatType seatType;
    private WaitlistStatus status;
    private Integer position;
    private LocalDateTime notifiedAt;
    private UUID reservationId;
    private LocalDateTime createdAt;
}