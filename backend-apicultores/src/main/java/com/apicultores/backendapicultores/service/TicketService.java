package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Ticket;

public interface TicketService {
    TicketResponse createTicket(CreateTicketRequest ticket);
}
