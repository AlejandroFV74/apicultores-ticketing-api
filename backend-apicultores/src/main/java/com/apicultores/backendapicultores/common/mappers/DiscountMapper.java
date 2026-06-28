package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.domain.dto.request.CreateDiscountRequest;
import com.apicultores.backendapicultores.domain.dto.response.discount.DiscountResponse;
import com.apicultores.backendapicultores.domain.entity.Discount;
import com.apicultores.backendapicultores.domain.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class DiscountMapper {
    public Discount toEntity(CreateDiscountRequest request, Event event) {
        return Discount.builder()
                .event(event)
                .code(request.getCode())
                .description(request.getDescription())
                .category(request.getCategory())
                .discountType(request.getDiscountType())
                .value(request.getValue())
                .minTickets(request.getMinTickets() != null ? request.getMinTickets() : 1)
                .validUntil(request.getValidUntil())
                .build();
    }

    public DiscountResponse toDto(Discount discount) {
        return DiscountResponse.builder()
                .discountId(discount.getDiscountId())
                .eventId(discount.getEvent().getEventId())
                .code(discount.getCode())
                .description(discount.getDescription())
                .category(discount.getCategory())
                .discountType(discount.getDiscountType())
                .value(discount.getValue())
                .minTickets(discount.getMinTickets())
                .validUntil(discount.getValidUntil())
                .build();
    }
}