package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.enums.PaymentStatus;
import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Refund;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.exception.custom.DueDateException;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.exception.custom.TicketStatusException;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.repository.RefundRepository;
import com.apicultores.backendapicultores.repository.TicketRepository;
import com.apicultores.backendapicultores.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CancelOrRefundTicketService {
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponse cancelAndRefund(RefundTicketRequest request){
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new TicketNotFoundException("El ticket solicitado no se encuentra disponible"));

        if (ticket.getStatus().equals(TicketStatus.USED)){
            throw new TicketStatusException("No se puede cancelar ni reembolsar un ticket ya usado");
        }

        Payment payment = ticket.getPayment();

        LocalDateTime dueDate = ticket.getSeat().getEvent().getEndDate();
        LocalDateTime cancellationDeadLine = dueDate.minusHours(48);

        if (LocalDateTime.now().isAfter(cancellationDeadLine)){
            throw new DueDateException("Solo está permitida la cancelación y reembolso hasta 48 horas antes del inicio del evento");
        }

        ticket.setStatus(TicketStatus.REFUNDED);
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        ticketRepository.save(ticket);

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .reason(request.getReason())
                .build();

        refundRepository.save(refund);
        return ticketMapper.toDto(ticket);
    }
}
