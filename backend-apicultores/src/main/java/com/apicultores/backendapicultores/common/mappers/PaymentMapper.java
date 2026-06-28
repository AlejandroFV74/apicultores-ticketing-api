package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.PaymentStatus;
import com.apicultores.backendapicultores.domain.dto.request.PaymentRequest;
import com.apicultores.backendapicultores.domain.dto.response.PaymentResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntityCreate(PaymentRequest request, Reservation reservation) {
        BigDecimal total = reservation.getSeats()
                .stream()
                .map(seat -> BigDecimal.valueOf(seat.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String provider = switch (request.getPaymentMethod().toUpperCase()) {
            case "STRIPE" -> "STRIPE";
            default -> "LOCAL";
        };

        return Payment.builder()
                .reservation(reservation)
                .amount(total)
                .paymentMethod(request.getPaymentMethod())
                .provider(provider)
                .providerReference(provider + "-" + UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .build();
    }

    public PaymentResponse toDto(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .reservationId(payment.getReservation().getReservationId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .provider(payment.getProvider())
                .providerReference(payment.getProviderReference())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
