package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.TransferTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.TicketTransferHistory;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.exception.custom.TicketStatusException;
import com.apicultores.backendapicultores.repository.TicketRepository;
import com.apicultores.backendapicultores.repository.TicketTransferHistoryRepository;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TicketTransferService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketTransferHistoryRepository transferHistoryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponse transferTicket(TransferTicketRequest request) {
        var ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new TicketNotFoundException("Ticket no encontrado"));

        if (ticket.getStatus() == null || ticket.getStatus().name().equals("USED") || ticket.getStatus().name().equals("REFUNDED")) {
            throw new TicketStatusException("No se puede transferir este ticket");
        }

        var currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null || !ticket.getOwner().getUserId().equals(currentUserId)) {
            throw new BadRequestException("Solo el propietario puede transferir el ticket");
        }

        User toUser = userRepository.findById(request.getToUserId())
                .orElseThrow(() -> new BadRequestException("Usuario destino no encontrado"));

        var fromUserId = ticket.getOwner().getUserId();

        ticket.setOwner(toUser);
        var saved = ticketRepository.save(ticket);

        TicketTransferHistory history = TicketTransferHistory.builder()
                .ticket(saved)
                .fromUserId(fromUserId)
                .toUserId(toUser.getUserId())
                .changedByUserId(currentUserId)
                .build();
        transferHistoryRepository.save(history);

        return ticketMapper.toDto(saved);
    }
}
