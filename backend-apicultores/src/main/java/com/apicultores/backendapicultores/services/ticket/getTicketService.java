package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.exceptions.TicketNotFoundException;
import com.apicultores.backendapicultores.repositories.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class getTicketService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketResponse getTicketById(UUID ticketId){
        return ticketMapper.toDto(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket no ha sido encontrado"))
        );
    }

    public List<TicketResponse> getTicketsByOwner(UUID ownerId){
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()){
            throw new TicketNotFoundException("No se ha encontrado ningún ticket");
        }

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

}
