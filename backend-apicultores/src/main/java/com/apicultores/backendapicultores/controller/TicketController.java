package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.services.TicketValidationService;
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
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketController {

    private final CreateTicketService createTicketService;
    private final CancelOrRefundTicketService cancelOrRefundTicketService;
    private final GetTicketService getTicketService;
    private final TicketValidationService ticketValidationService;

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

    @GetMapping()
    public ResponseEntity<GeneralResponse> getTickets(){
            return buildResponse(
                    "Se han obtenido los tickets",
                    HttpStatus.OK,
                    getTicketService.getAllTickets()
            );

        /*
        return buildResponse(
                "Se han obtenido el ticket",
                HttpStatus.OK,
                getTicketService.getTicketById(ticket_id)
        );
        */
    }

    @GetMapping("/owner/{owner_id}")
    public ResponseEntity<GeneralResponse> getTicketByOwner(@PathVariable(required = true) UUID owner_id){
        return buildResponse(
                "Se han obtenido el ticket",
                HttpStatus.OK,
                getTicketService.getTicketsByOwnerId(owner_id)
        );
    }

    @PostMapping("/validation/{qr_code}")
    public ResponseEntity<GeneralResponse> validateTicket(@PathVariable(required = true) String qr_code){
        return buildResponse(
                "Se ha cobrado el ticket",
                HttpStatus.OK,
                ticketValidationService.validateAndAccess(qr_code)
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
