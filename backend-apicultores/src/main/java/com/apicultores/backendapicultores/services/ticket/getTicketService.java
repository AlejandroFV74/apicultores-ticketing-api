package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.repositories.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class getTicketService {
    private final TicketRepository ticketRepository;

    public Ticket getTicketById(UUID ticketId){
        return;
    }
}
