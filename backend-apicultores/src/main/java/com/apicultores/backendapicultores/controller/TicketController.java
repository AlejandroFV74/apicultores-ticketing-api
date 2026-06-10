package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.TicketResponse;
import com.apicultores.backendapicultores.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<List<TicketResponse>> findAllTickets() {
        return ResponseEntity.ok(ticketService.findAllTickets());
    }
}
