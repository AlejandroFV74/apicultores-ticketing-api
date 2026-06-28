package com.apicultores.backendapicultores.services;

import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketValidationResponse;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.repository.TicketRepository;
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
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketValidationResponse validateAndAccess(String qrCode){
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new TicketNotFoundException("El código QR no pertenece a nuestra empresa"));

        validationSteps.forEach( step -> step.validate(ticket));

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return ticketMapper.toValidationDto("Ticket registrado, Disfrute!");
    }
}
