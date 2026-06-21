package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationMapper {
    public Reservation toEntityCreate(User user, List<Seat> seats){
        return Reservation.builder()
                .user(user)
                .seats(seats)
                .status(ReservationStatus.ACTIVE)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(15)
                )
                .build();
    }

    public ReservationResponse toDto(Reservation reservation){
        return ReservationResponse.builder()
                .id(reservation.getReservationId())
                .seats(reservation.getSeats())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .experiesAt(reservation.getExpiresAt())
                .build();
    }
}
