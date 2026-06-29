package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.enums.TicketStatus;
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

        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new TicketStatusException("Solo se puede transferir un ticket pagado");
        }

        var currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null || !ticket.getOwner().getUserId().equals(currentUserId)) {
            throw new BadRequestException("Solo el propietario puede transferir el ticket");
        }

        String toUserEmail = normalizeEmail(request.getToUserEmail());
        User toUser = userRepository.findByEmail(toUserEmail)
                .orElseThrow(() -> new BadRequestException("Usuario destino no encontrado"));

        var fromUserId = ticket.getOwner().getUserId();
        if (fromUserId.equals(toUser.getUserId())) {
            throw new BadRequestException("No puedes transferir el ticket al mismo propietario");
        }

        ticket.setOwner(toUser);
        ticket.setStatus(TicketStatus.PAID);
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

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("El email del usuario destino es obligatorio");
        }
        return email.trim().toLowerCase();
    }
}
