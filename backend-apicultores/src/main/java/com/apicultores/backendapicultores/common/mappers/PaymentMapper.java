package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.PaymentStatus;
import com.apicultores.backendapicultores.domain.dto.request.PaymentRequest;
import com.apicultores.backendapicultores.domain.dto.response.PaymentResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntityCreate(PaymentRequest request, Reservation reservation) {
        return Payment.builder()
                .reservation(reservation)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .provider(request.getProvider())
                .providerReference(request.getProviderReference())
                .status(PaymentStatus.COMPLETED)
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
