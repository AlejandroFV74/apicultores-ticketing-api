package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SeatMapper {

    public Seat toEntity(CreateSeatRequest request, Event event) {
        return Seat.builder()
                .event(event)
                .seatNumber(request.getSeatNumber())
                .seatType(request.getSeatType())
                .price(request.getPrice())
                .status(SeatStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Seat toEntityUpdate(UpdateSeatRequest request, Seat seat) {
        if (request.getSeatNumber() != null) {
            seat.setSeatNumber(request.getSeatNumber());
        }
        if (request.getSeatType() != null) {
            seat.setSeatType(request.getSeatType());
        }
        if (request.getPrice() != null) {
            seat.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            seat.setStatus(request.getStatus());
        }
        return seat;
    }

    public SeatResponse toDto(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getSeatId())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .price(seat.getPrice())
                .status(seat.getStatus())
                .createdAt(seat.getCreatedAt())
                .build();
    }
}
