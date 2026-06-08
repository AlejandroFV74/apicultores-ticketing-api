package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.TicketStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entities.Reservation;
import com.apicultores.backendapicultores.domain.entities.Seat;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    public Ticket toEntityCreate(Reservation reservation, Seat seat, String qrCode){
        return Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .owner(reservation.getUser())
                .qrCode(qrCode)
                .status(TicketStatus.ACTIVE)
                .build();

    }

    public TicketResponse toDto(Ticket ticket){
        return TicketResponse.builder()
                .qrCode(ticket.getQrCode())
                .status(ticket.getStatus())
                .usedAt(ticket.getUsedAt())
                .createdAt(ticket.getCreatedAt())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .seatType(ticket.getSeat().getSeatType())
                //No implementado aún, falta definir las entidades
                .eventName(ticket.getSeat().getEvent().getName())
                .eventDate(ticket.getSeat().getEvent().getStartDate())
                .ownerName(ticket.getOwner().getName())
                .build();
    }
}
