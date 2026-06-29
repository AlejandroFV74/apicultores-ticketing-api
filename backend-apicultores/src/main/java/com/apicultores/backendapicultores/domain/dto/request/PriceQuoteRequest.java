package com.apicultores.backendapicultores.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PriceQuoteRequest {
    @NotNull
    private UUID eventId;

    @NotNull
    private List<UUID> seatIds;

    private String code;
}