package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.SeatType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class JoinWaitlistRequest {
    @NotNull
    private UUID eventId;
    @NotNull
    private SeatType seatType;
}