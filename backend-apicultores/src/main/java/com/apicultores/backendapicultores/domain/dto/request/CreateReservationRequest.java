package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.domain.entity.Seat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {
    @NotNull(message = "Event id is required")
    private UUID eventId;
    @NotNull(message = "Asientos no deben ser nulos")
    private List<UUID> seatsIds;
}
