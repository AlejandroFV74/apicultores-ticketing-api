package com.apicultores.backendapicultores.domain.dto.response.discount;
import com.apicultores.backendapicultores.common.enums.DiscountCategory;
import com.apicultores.backendapicultores.common.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse {
    private UUID discountId;
    private UUID eventId;
    private String code;
    private String description;
    private DiscountCategory category;
    private DiscountType discountType;
    private BigDecimal value;
    private Integer minTickets;
    private LocalDateTime validUntil;
}