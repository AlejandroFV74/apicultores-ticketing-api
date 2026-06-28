package com.apicultores.backendapicultores.domain.dto.response.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceQuoteResponse {
    private BigDecimal subtotal;
    private List<AppliedDiscount> appliedDiscounts;
    private BigDecimal totalDiscount;
    private BigDecimal total;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppliedDiscount {
        private String description;
        private String category;
        private BigDecimal amountDiscounted;
    }
}