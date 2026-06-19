package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.services.ticket.CancelOrRefundTicketService;
import com.apicultores.backendapicultores.services.ticket.CreateTicketService;
import com.apicultores.backendapicultores.services.ticket.GetTicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tickets")
@AllArgsConstructor
public class TicketController {

    private final CreateTicketService createTicketService;
    private final CancelOrRefundTicketService cancelOrRefundTicketService;
    private final GetTicketService getTicketService;

    @PostMapping("/generate")
    public ResponseEntity<GeneralResponse> generateTicket(@RequestBody CreateTicketRequest request){
        return buildResponse(
                "Tickets creados exitosamente",
                HttpStatus.CREATED,
                createTicketService.generateTicketsForReservation(request)
        );
    }

    @PostMapping("/cancel-refund")
    public ResponseEntity<GeneralResponse> cancelAndRefundTicket(@RequestBody RefundTicketRequest request){
        return buildResponse(
                "El ticket ha sido cancelado y reembolsado con éxito",
                HttpStatus.OK,
                cancelOrRefundTicketService.cancelAndRefund(request)
        );
    }

    @GetMapping("/{ticket_id}")
    public ResponseEntity<GeneralResponse> getTickets(@RequestParam(required = false) UUID ticket_id){

        if (ticket_id == null){
            return buildResponse(
                    "Se han obtenido los tickets",
                    HttpStatus.OK,
                    getTicketService.getAllTickets()
            );
        }

        return buildResponse(
                "Se han obtenido el ticket",
                HttpStatus.OK,
                getTicketService.getTicketById(ticket_id)
        );
    }

    @GetMapping("/owner/{owner_id}")
    public ResponseEntity<GeneralResponse> getTicketByOwner(@RequestParam(required = true) UUID owner_id){
        return buildResponse(
                "Se han obtenido el ticket",
                HttpStatus.OK,
                getTicketService.getTicketsByOwner(owner_id)
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
