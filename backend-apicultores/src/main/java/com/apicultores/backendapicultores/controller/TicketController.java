package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.services.ticket.CreateTicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/tickets")
@AllArgsConstructor
public class TicketController {

    private final CreateTicketService createTicketService;

    @PostMapping("/generate")
    public ResponseEntity<GeneralResponse> generateTicket(@RequestBody CreateTicketRequest request){
        return buildResponse(
                "Tickets creados exitosamente",
                HttpStatus.CREATED,
                createTicketService.generateTicketsForReservation(request)
        );
    }





    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }
}
