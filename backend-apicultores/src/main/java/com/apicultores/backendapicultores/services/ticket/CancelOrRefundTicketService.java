package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.enums.PaymentStatus;
import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Refund;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.DueDateException;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.exception.custom.TicketStatusException;
import com.apicultores.backendapicultores.repository.*;
import com.apicultores.backendapicultores.services.waitlist.WaitlistService;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.repository.RefundRepository;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CancelOrRefundTicketService {
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final TicketMapper ticketMapper;
    private final SeatRepository seatRepository;
    private final WaitlistService waitlistService;

    @Transactional
    public TicketResponse cancelAndRefund(RefundTicketRequest request){
        validateRequest(request);

        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new TicketNotFoundException("El ticket solicitado no se encuentra disponible"));

        validateTicketCanBeRefunded(ticket, request);

        Payment payment = ticket.getPayment();
        BigDecimal refundAmount = BigDecimal.valueOf(ticket.getSeat().getPrice());

        ticket.setStatus(TicketStatus.REFUNDED);
        ticket.getSeat().setStatus(SeatStatus.AVAILABLE);

        ticketRepository.save(ticket);
        seatRepository.save(ticket.getSeat());

        updatePaymentAndReservationStatus(payment);

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(refundAmount)
                .reason(request.getReason().trim())
                .build();

        refundRepository.save(refund);

        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
        waitlistService.processFreedSeat(seat.getEvent().getEventId(), seat.getSeatType());
        return ticketMapper.toDto(ticket);
    }

    private void validateRequest(RefundTicketRequest request) {
        if (request == null || request.getTicketId() == null) {
            throw new BadRequestException("El ticket es obligatorio para solicitar reembolso");
        }
    }

    private void validateTicketCanBeRefunded(Ticket ticket, RefundTicketRequest request) {
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new BadRequestException("La razón del reembolso es obligatoria");
        }

        if (ticket.getStatus() != TicketStatus.PAID){
            throw new TicketStatusException("Solo se puede reembolsar un ticket pagado");
        }

        UUID currentUserId = currentUserProvider.getCurrentUserId();
        if (!isCurrentAdmin() && (currentUserId == null || !ticket.getOwner().getUserId().equals(currentUserId))) {
            throw new BadRequestException("Solo el propietario o un administrador puede reembolsar el ticket");
        }

        if (ticket.getSeat().getPrice() == null) {
            throw new BadRequestException("El ticket no tiene un precio válido para reembolso");
        }

        LocalDateTime eventStartDate = ticket.getSeat().getEvent().getStartDate();
        if (eventStartDate == null) {
            throw new BadRequestException("El evento no tiene fecha de inicio configurada");
        }

        LocalDateTime cancellationDeadline = eventStartDate.minusHours(48);
        if (LocalDateTime.now().isAfter(cancellationDeadline)){
            throw new DueDateException("Solo está permitida la cancelación y reembolso hasta 48 horas antes del inicio del evento");
        }
    }

    private void updatePaymentAndReservationStatus(Payment payment) {
        List<Ticket> paymentTickets = ticketRepository.findByPaymentWithEagerLoad(payment.getPaymentId());
        boolean allPaymentTicketsRefunded = paymentTickets.stream()
                .allMatch(ticket -> ticket.getStatus() == TicketStatus.REFUNDED);

        payment.setStatus(allPaymentTicketsRefunded
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED);
        paymentRepository.save(payment);

        var reservation = payment.getReservation();
        List<Ticket> reservationTickets = ticketRepository.findByReservationWithEagerLoad(reservation.getReservationId());
        boolean allReservationTicketsRefunded = reservationTickets.stream()
                .allMatch(ticket -> ticket.getStatus() == TicketStatus.REFUNDED);

        if (allReservationTicketsRefunded) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        }
    }
    private boolean isCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
