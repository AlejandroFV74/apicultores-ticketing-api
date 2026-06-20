package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.exception.custom.TicketNotFoundException;
import com.apicultores.backendapicultores.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GetTicketService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketResponse getTicketById(UUID ticketId){
        return ticketMapper.toDto(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket no ha sido encontrado"))
        );
    }

    public List<TicketResponse> getTicketsByOwnerId(UUID ownerId){
        List<Ticket> tickets = ticketRepository.findByOwner_UserId(ownerId)
                .orElseThrow(() -> new TicketNotFoundException("El usuario no tiene tickets"));


        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getAllTickets(){
        List<Ticket> tickets = ticketRepository.findAllWithSeat().orElseThrow(
                () -> new TicketNotFoundException("No se encontraron tickets"));
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getUsedTicketByOwner(UUID uuid){
        List<Ticket> tickets = ticketRepository.findByStatusUsedAndOwner(uuid).
                orElseThrow(() -> new TicketNotFoundException("No se han encontrado tickets usados"));
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getActiveTicketByOwner(UUID uuid){
        List<Ticket> tickets = ticketRepository.findActiveTicketsByOwner(uuid).
                orElseThrow(() -> new TicketNotFoundException("No se han encontrado tickets Activos"));
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

}
