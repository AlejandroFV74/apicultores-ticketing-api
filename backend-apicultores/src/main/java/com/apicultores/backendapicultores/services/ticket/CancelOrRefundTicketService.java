package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.TicketStatus;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entities.Payment;
import com.apicultores.backendapicultores.domain.entities.Refund;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.domain.entities.User;
import com.apicultores.backendapicultores.exceptions.TicketNotFoundException;
import com.apicultores.backendapicultores.exceptions.TicketStatusException;
import com.apicultores.backendapicultores.exceptions.UserNotFoundException;
import com.apicultores.backendapicultores.repositories.PaymentRepository;
import com.apicultores.backendapicultores.repositories.RefundRepository;
import com.apicultores.backendapicultores.repositories.TicketRepository;
import com.apicultores.backendapicultores.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

        ticket.setStatus(TicketStatus.REFUNDED);
        ticketRepository.save(ticket);

        //Añadir el status del payment a REFUNDED cuando este implementado

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(request.getAmount())
                .reason(request.getReason())
                .build();

        refundRepository.save(refund);
        return ticketMapper.toDto(ticket);
    }
}
