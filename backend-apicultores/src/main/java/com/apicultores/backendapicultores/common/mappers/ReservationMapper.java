package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {
    public Reservation toEntityCreate(User user, List<Seat> seats, Event event){
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("Al menos se necesita un asiento");
        }

        for (Seat s : seats) {
            if (s.getEvent() == null || !s.getEvent().getEventId().equals(event.getEventId())) {
                throw new IllegalArgumentException("Todos los asientos deben pertencer al mismo evento");
            }
        }

        return Reservation.builder()
                .user(user)
                .event(event)
                .seats(seats)
                .status(ReservationStatus.ACTIVE)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(15)
                )
                .build();
    }

    public ReservationResponse toDto(Reservation reservation){
        List<SeatResponse> seatsDto = reservation.getSeats()
                .stream()
                .map(s -> SeatResponse.builder()
                        .id(s.getSeatId())
                        .seatNumber(s.getSeatNumber())
                        .seatType(s.getSeatType())
                        .price(s.getPrice())
                        .status(s.getStatus())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ReservationResponse.builder()
                .id(reservation.getReservationId())
                .eventId(reservation.getEvent().getEventId())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .expiresAt(reservation.getExpiresAt())
                .seats(seatsDto)
                .build();
    }
}
