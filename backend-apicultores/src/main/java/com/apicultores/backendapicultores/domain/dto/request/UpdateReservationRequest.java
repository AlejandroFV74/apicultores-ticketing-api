package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {
    private List<UUID> seatsIds;
    private ReservationStatus status;
}
