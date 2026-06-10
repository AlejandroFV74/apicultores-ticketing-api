package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class TicketMapper {

    public Ticket toEntityCreate(CreateTicketRequest request) {

        User owner = new User();
        owner.setId(request.getOwnerId());

        Reservation reservation = new Reservation();
        reservation.setId(request.getReservationId());

        Seat seat = new Seat();
        seat.setId(request.getSeatId());

        return Ticket.builder()
                .owner(owner)
                .reservation(reservation)
                .seat(seat)
                .ticketStatus(TicketStatus.AVAILABLE)
                .qrToken(UUID.randomUUID())
                .build();
    }

    public TicketResponse toDto(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .status(ticket.getTicketStatus())
                .message("Ticket found")
                .build();
    }
}
