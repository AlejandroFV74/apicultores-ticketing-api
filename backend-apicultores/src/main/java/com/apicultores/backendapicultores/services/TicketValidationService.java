package com.apicultores.backendapicultores.services;

import com.apicultores.backendapicultores.exceptions.TicketNotFoundException;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.common.TicketStatus;
import com.apicultores.backendapicultores.repositories.TicketRepository;
import com.apicultores.backendapicultores.validation.TicketValidationStep;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketValidationService {
    private final TicketRepository ticketRepository;
    private final List<TicketValidationStep> validationSteps;

    @Transactional
    public Ticket validateAndAccess(String qrCode){
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new TicketNotFoundException("El código QR no es válido"));

        validationSteps.forEach( step -> step.validate(ticket));

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }
}
