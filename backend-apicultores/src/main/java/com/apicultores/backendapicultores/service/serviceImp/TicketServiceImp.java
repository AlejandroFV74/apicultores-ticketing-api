package com.apicultores.backendapicultores.service.serviceImp;

import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.repository.TicketRepository;
import com.apicultores.backendapicultores.service.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImp implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket1 = ticketMapper.toEntityCreate(request);
        return TicketResponse.builder()
                .valid(true)
                .message("Ticket created successfully")
                .build();
    }
}
