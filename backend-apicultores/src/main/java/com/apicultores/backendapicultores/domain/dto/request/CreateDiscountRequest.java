package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.DiscountCategory;
import com.apicultores.backendapicultores.common.enums.DiscountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateDiscountRequest {
    @NotNull
    private UUID eventId;

    private String code;

    private String description;

    @NotNull
    private DiscountCategory category;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal value;

    private Integer minTickets;

    private LocalDateTime validUntil;
}