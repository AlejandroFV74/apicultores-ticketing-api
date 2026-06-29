package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.request.RefundTicketRequest;
import com.apicultores.backendapicultores.domain.dto.request.TransferTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.services.TicketValidationService;
import com.apicultores.backendapicultores.services.ticket.CancelOrRefundTicketService;
import com.apicultores.backendapicultores.services.ticket.CreateTicketService;
import com.apicultores.backendapicultores.services.ticket.GetTicketService;
import com.apicultores.backendapicultores.services.ticket.TicketTransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TicketTransferService ticketTransferService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate")
    public ResponseEntity<GeneralResponse> generateTicket(@RequestBody Reservation reservation, Payment payment){
        return buildResponse(
                "Tickets creados exitosamente",
                HttpStatus.CREATED,
                createTicketService.generateTicketsForReservation(reservation, payment)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cancel-refund")
    public ResponseEntity<GeneralResponse> cancelAndRefundTicket(@RequestBody RefundTicketRequest request){
        return buildResponse(
                "El ticket ha sido cancelado y reembolsado con éxito",
                HttpStatus.OK,
                cancelOrRefundTicketService.cancelAndRefund(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<GeneralResponse> getTickets(){
            return buildResponse(
                    "Se han obtenido los tickets",
                    HttpStatus.OK,
                    getTicketService.getAllTickets()
            );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/owner/{owner_id}")
    public ResponseEntity<GeneralResponse> getTicketByOwner(@PathVariable(required = true) UUID owner_id){
      return buildResponse(
                "Tickets del usuario obtenidos",
                HttpStatus.OK,
                getTicketService.getTicketsByOwnerId(owner_id)
        );
    }

  
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/transfer")
    public ResponseEntity<GeneralResponse> transferTicket(@RequestBody TransferTicketRequest request){
        return buildResponse(
                "Ticket transferido",
                HttpStatus.OK,
                ticketTransferService.transferTicket(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/validation/{qr_code}")
    public ResponseEntity<GeneralResponse> validateTicket(@PathVariable(required = true) String qr_code){
        return buildResponse(
                "Se ha cobrado el ticket",
                HttpStatus.OK,
                ticketValidationService.validateAndAccess(qr_code)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/history/{owner_id}")
    public ResponseEntity<GeneralResponse> getHistoryTickets(@PathVariable(required = true) UUID owner_id){
        return buildResponse(
                "se han encontrado tickets usados",
                HttpStatus.OK,
                getTicketService.getTicketsByOwnerId(owner_id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    @GetMapping("/mytickets/{owner_id}")
    public ResponseEntity<GeneralResponse>  getMyTickets(@PathVariable(required = true) UUID owner_id){
        return buildResponse(
                "Se han encontrado tickets activos",
                HttpStatus.OK,
                getTicketService.getActiveTicketByOwner(owner_id)
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
