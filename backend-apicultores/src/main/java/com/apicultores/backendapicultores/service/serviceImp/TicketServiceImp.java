package com.apicultores.backendapicultores.service.serviceImp;

import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.repository.TicketRepository;
import com.apicultores.backendapicultores.service.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImp implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        return ticketMapper.toDto(
            ticketRepository.save(ticketMapper.toEntityCreate(request))
        );
    }

    @Override
    @Transactional
    public List<TicketResponse> findAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());

    }
}
