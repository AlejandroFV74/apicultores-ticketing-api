package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Refund;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.exception.custom.TicketStatusException;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.repository.RefundRepository;
import com.apicultores.backendapicultores.repository.TicketRepository;
import com.apicultores.backendapicultores.repository.UserRepository;
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
