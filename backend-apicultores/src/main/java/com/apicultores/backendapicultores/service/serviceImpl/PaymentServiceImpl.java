package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.mappers.PaymentMapper;
import com.apicultores.backendapicultores.domain.dto.request.PaymentRequest;
import com.apicultores.backendapicultores.domain.dto.response.PaymentResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        validateRequest(request);

        Reservation reservation = reservationRepository.findById(request.getReservation_id())
                .orElseThrow(() -> new ResourceNotFoundException("La reserva no fue encontrada"));

        paymentRepository.findByReservationReservationId(reservation.getReservationId())
                .ifPresent(payment -> {
                    throw new BadRequestException("La reserva ya tiene un pago asociado");
                });

        Payment payment = paymentMapper.toEntityCreate(request, reservation);

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        for (Seat seat : reservation.getSeats()) {
            seat.setStatus(SeatStatus.SOLD);
        }

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();

        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron pagos");
        }

        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("El pago no fue encontrado"));

        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponse getPaymentByReservationId(UUID reservationId) {
        Payment payment = paymentRepository.findByReservationReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro pago para esta reserva"));

        return paymentMapper.toDto(payment);
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getReservation_id() == null) {
            throw new BadRequestException("La reserva es obligatoria");
        }

        if (isBlank(request.getPaymentMethod())) {
            throw new BadRequestException("El metodo de pago es obligatorio");
        }

//        if (isBlank(request.getProvider())) {
//            throw new BadRequestException("El proveedor es obligatorio");
//        }
//
//        if (isBlank(request.getProviderReference())) {
//            throw new BadRequestException("La referencia del proveedor es obligatoria");
//        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
