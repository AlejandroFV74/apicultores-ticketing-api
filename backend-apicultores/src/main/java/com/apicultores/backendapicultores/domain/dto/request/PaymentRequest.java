package com.apicultores.backendapicultores.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private UUID reservation_id;
    private BigDecimal amount;
    private String paymentMethod;
    private String provider;
    private String providerReference;
}
