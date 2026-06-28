package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.PaymentNotFoundException;
import com.apicultores.backendapicultores.exception.custom.ReservationStatusException;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.services.ticket.CreateTicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final CreateTicketService ticketService;

    @Transactional
    public List<TicketResponse> confirmCheckout(UUID paymentId){

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Pago no encontrado"));

        Reservation reservation = payment.getReservation();

        // Evitar confirmar dos veces
        if(reservation.getStatus() == ReservationStatus.COMPLETED){
            throw new BadRequestException("La reserva ya fue completada");
        }

        // Validar expiración
        if(reservation.getStatus() == ReservationStatus.EXPIRED){
            throw new ReservationStatusException("La reserva expiró");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);

        for(Seat seat : reservation.getSeats()){
            seat.setStatus(SeatStatus.SOLD);
        }

        seatRepository.saveAll(reservation.getSeats());
        reservationRepository.save(reservation);

        return ticketService.generateTicketsForReservation(
                reservation,
                payment
        );
    }
}
